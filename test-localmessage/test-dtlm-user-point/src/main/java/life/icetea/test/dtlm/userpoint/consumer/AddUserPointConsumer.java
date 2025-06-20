package life.icetea.test.dtlm.userpoint.consumer;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import life.icetea.test.dtlm.common.AddUserPointMessage;
import life.icetea.test.dtlm.userpoint.entity.TbUserPoint;
import life.icetea.test.dtlm.userpoint.entity.TbUserPointStream;
import life.icetea.test.dtlm.userpoint.mapper.TbUserPointMapper;
import life.icetea.test.dtlm.userpoint.mapper.TbUserPointStreamMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Optional;

@Component
@Slf4j
@RocketMQMessageListener(topic = "dtlm_topic", consumerGroup = "dtlm_group", maxReconsumeTimes = 6)
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
                .eq(TbUserPointStream::getIdentifier, msg.getOrderId())
                .oneOpt();
        if (tbUserPointStream.isPresent()) {
            log.warn("以处理过该消息，msg={}", msg);
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

        // 请求修改message state
        try {
//            int i = 1 / 0;
            HashMap<String, Object> params = MapUtil.of("msgId", msg.getMsgId());
            String result = HttpUtil.post("http://localhost:8081/create_order/finished", params);
//           int i = 1/0; // 上一步请求也是幂等操作所以多次调用不会造成影响
            log.info("修改本地消息表成功: {}", result);
        } catch (Exception e) {
            log.error("修改本地消息表异常： msg={}", msg);
            throw e; //抛出异常让rocketmq重试，因为上一步是幂等操作，所以重试不会重复增加积分
        }
    }

    private TbUserPoint addUserPoint(AddUserPointMessage msg) {
        tbUserPointMapper.addUserPoint(msg.getUserId());
        return tbUserPointMapper.selectById(msg.getUserId());
    }

    private void insertStream(AddUserPointMessage msg, TbUserPoint userPoint) {
        TbUserPointStream stream = new TbUserPointStream();
        stream.setIdentifier(msg.getOrderId().toString());
        TbUserPointStream.UserPointStreamRecord record = new TbUserPointStream.UserPointStreamRecord();
        record.setTo(userPoint.getPoint());
        record.setFrom(userPoint.getPoint() - 10);
        stream.setRecord(record);

        tbUserPointStreamMapper.insert(stream);
    }

}
