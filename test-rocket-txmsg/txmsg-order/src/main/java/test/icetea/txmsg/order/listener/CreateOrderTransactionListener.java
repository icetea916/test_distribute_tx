package test.icetea.txmsg.order.listener;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import test.icetea.txmsg.common.AddUserPointMessage;
import test.icetea.txmsg.order.domain.entity.TbOrder;
import test.icetea.txmsg.order.mapper.TbOrderMapper;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@RocketMQTransactionListener(rocketMQTemplateBeanName = "myRocketMQTemplate")
@Slf4j
public class CreateOrderTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        AddUserPointMessage payload = null;
        try {
            String jsonStr = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
            payload = objectMapper.readValue(jsonStr, AddUserPointMessage.class);
        } catch (JsonProcessingException e) {
            log.error("反序列化失败. msg={}", JSONUtil.toJsonStr(msg), e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        // 创建订单
        try {
            log.info("创建订单， payload={}, msgHeader={}", payload, msg.getHeaders());

//            int i = 1 / 0; // 模拟插入失败
            TbOrder tbOrder = new TbOrder();
            tbOrder.setUserId(1L);
            tbOrder.setOrderNo(payload.getOrderNo());
            tbOrder.setGmtCreate(new Date());
            tbOrderMapper.insert(tbOrder);
//            int i = 1 / 0; // 模拟提事务消息失败, rocketmq会通过回查checkLocalTransaction方法检测订单是否创建成功
            log.info("创建订单成功，提交事务消息， payload={}", payload);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("创建订单失败. payload={}, msgHeader={}", payload, msg.getHeaders(), e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        AddUserPointMessage payload = null;
        try {
            String jsonStr = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
            payload = objectMapper.readValue(jsonStr, AddUserPointMessage.class);
        } catch (JsonProcessingException e) {
            log.error("反序列化失败. msg={}", JSONUtil.toJsonStr(msg), e);
            throw new RuntimeException(e);
        }

        log.info("检查本地事务，订单是否创建成功. payload={}, msgHeader={}", payload, msg.getHeaders());
        Optional<TbOrder> tbOrderOptional = ChainWrappers.lambdaQueryChain(tbOrderMapper)
                .eq(TbOrder::getOrderNo, payload.getOrderNo())
                .oneOpt();
        if (!tbOrderOptional.isPresent()) {
            log.info("订单未创建成功,回滚事务消息. payload={}", payload);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        log.info("订单已创建成功. payload={}", payload);
        return RocketMQLocalTransactionState.COMMIT;
    }

}

