package test.icetea.txmsg.order.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.icetea.txmsg.common.AddUserPointMessage;
import test.icetea.txmsg.order.mapper.TbOrderMapper;
import test.icetea.txmsg.order.template.MyRocketMQTemplate;

import java.util.Map;

@RestController
@RequestMapping("order")
@Slf4j
public class CreateOrderMsgController {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private MyRocketMQTemplate myRocketMQTemplate;

    @Value("${test.create-order-topic}")
    private String topicName;

    @RequestMapping("create")
    @Transactional
    public Map<String, Object> createOrder() {
        // 发送增加用户积分消息
        String orderNo = IdUtil.getSnowflakeNextIdStr();
        Message<AddUserPointMessage> message = MessageBuilder.withPayload(new AddUserPointMessage(1L, orderNo))
                .build();

        log.info("发送半事务消息, payload={}", message.getPayload());
        TransactionSendResult sendResult = null;
        try {
            sendResult = myRocketMQTemplate.sendMessageInTransaction(topicName, message, null);
        } catch (Exception e) {
            log.error("发送半事务消息失败, payload={}", message.getPayload(), e);
            throw new RuntimeException(e);
        }

        Map<String, Object> result = MapUtil.<String, Object>builder()
                .put("orderNo", orderNo)
                .put("success", true)
                .build();
        return result;
    }

}
