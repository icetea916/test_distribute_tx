package test.icetea.txmsg.userpoint.consumer;


import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import test.icetea.txmsg.common.AddUserPointMessage;
import test.icetea.txmsg.userpoint.domain.entity.TbUserPoint;
import test.icetea.txmsg.userpoint.domain.entity.TbUserPointStream;
import test.icetea.txmsg.userpoint.mapper.TbUserPointMapper;
import test.icetea.txmsg.userpoint.mapper.TbUserPointStreamMapper;

import java.util.Optional;

@Component
@Slf4j
@RocketMQMessageListener(topic = "txmsg_topic", consumerGroup = "txmsg_consumer_group", maxReconsumeTimes = 2)
public class AddUserPointConsumer implements RocketMQListener<AddUserPointMessage> {

    @Autowired
    public TbUserPointMapper tbUserPointMapper;
    @Autowired
    public TbUserPointStreamMapper tbUserPointStreamMapper;
    @Autowired
    public PlatformTransactionManager transactionManager;
    @Autowired
    public TransactionDefinition transactionDefinition;

    @Override
    public void onMessage(AddUserPointMessage msg) {
        // 幂等校验是否以增加积分
        Optional<TbUserPointStream> tbUserPointStream = ChainWrappers
                .lambdaQueryChain(tbUserPointStreamMapper)
                .eq(TbUserPointStream::getIdentifier, msg.getOrderNo())
                .oneOpt();
        if (tbUserPointStream.isPresent()) {
            log.warn("以处理过该订单，msg={}", msg);
            return;
        }

        // 增加积分，插入积分流水 2步都在一个事务中
        TransactionStatus ts = transactionManager.getTransaction(transactionDefinition);
        try {
//            int i = 1 / 0;
            TbUserPoint userPoint = addUserPoint(msg);
//            int i = 1 / 0;
            insertStream(msg, userPoint);
            log.info("增加用户积分成功， {}", msg);
            transactionManager.commit(ts);
        } catch (Exception e) {
            log.error("增加用户积分异常，{}", msg);
            transactionManager.rollback(ts);
            throw e; // 抛出异常让rocketmq进行重试
        }
    }

    private TbUserPoint addUserPoint(AddUserPointMessage msg) {
        tbUserPointMapper.addUserPoint(msg.getUserId());
        return tbUserPointMapper.selectById(msg.getUserId());
    }

    private void insertStream(AddUserPointMessage msg, TbUserPoint userPoint) {
        TbUserPointStream stream = new TbUserPointStream();
        stream.setIdentifier(msg.getOrderNo());
        TbUserPointStream.UserPointStreamRecord record = new TbUserPointStream.UserPointStreamRecord();
        record.setTo(userPoint.getPoint());
        record.setFrom(userPoint.getPoint() - 10);
        stream.setRecord(record);

        tbUserPointStreamMapper.insert(stream);
    }

}
