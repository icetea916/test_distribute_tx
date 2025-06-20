package life.icetea.test.dtlm.order.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import life.icetea.test.dtlm.common.AddUserPointMessage;
import life.icetea.test.dtlm.order.domain.entity.TbCreateOrderMessage;
import life.icetea.test.dtlm.order.domain.entity.TbOrder;
import life.icetea.test.dtlm.order.mapper.TbCreateOrderMessageMapper;
import life.icetea.test.dtlm.order.mapper.TbOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbCreateOrderMessageMapper tbCreateOrderMessageMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;

    @Value("${test.create-order-topic}")
    private String topicName;

    @Autowired
    private TaskExecutor taskExecutor;

    @RequestMapping("create")
    @Transactional
    public Map<String, Object> createOrder() {
        TransactionStatus txStatus = transactionManager.getTransaction(transactionDefinition);
        TbOrder order = null;
        TbCreateOrderMessage msg = null;
        try {
            // 创建订单
            order = insertOrder();
//            int i = 1 / 0;
            // 插入本地消息表（解决发送消息异常导致的一致性问题）
            msg = insertTbCreateOrderMessage(order);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            log.error("create order error. order={}, msg={}", JSONUtil.toJsonStr(order), JSONUtil.toJsonStr(msg), e);
            transactionManager.rollback(txStatus);
            throw e;
        }


        final Long userId = order.getUserId();
        final Long orderId = order.getId();
        final Long msgId = msg.getId();
        // 异步发消息
        taskExecutor.execute(() -> {
            String rqMsgId = null;
            try {
                // 发送增加用户积分消息
//                int i = 1 / 0;
                AddUserPointMessage addUserPointMessage = new AddUserPointMessage();
                addUserPointMessage.setMsgId(msgId);
                addUserPointMessage.setUserId(userId);
                addUserPointMessage.setOrderId(orderId);
                SendResult sendResult = rocketMQTemplate.syncSend(topicName, addUserPointMessage);
                rqMsgId = sendResult.getMsgId();
                log.info("发送消息成功，orderId={}, rqMsgId={}", orderId, sendResult.getMsgId());
                // 修改msg状态为已发送,若失败定时任务重试，由于consumer是幂等所以补偿重新发送不会出现数据不一致问题
//                int i = 1/0;
                tbCreateOrderMessageMapper.updateStateToPublished(msgId, rqMsgId);
                log.info("修改消息状态成功，orderId={}, msgId={}, rqMsgId={}", orderId, msgId, rqMsgId);
            } catch (Exception e) {
                // 发送失败修改msg状态
                log.error("发送消息异常, orderId={}, msgId={}", orderId, msgId, e);
                tbCreateOrderMessageMapper.updateStateToFailed(msgId, ExceptionUtil.stacktraceToString(e));
            }
        });

        Map<String, Object> result = MapUtil.<String, Object>builder()
                .put("order", JSONUtil.toJsonStr(order))
                .put("success", true)
                .build();
        return result;
    }

    private TbCreateOrderMessage insertTbCreateOrderMessage(TbOrder order) {
        TbCreateOrderMessage msg = new TbCreateOrderMessage();
        msg.setGmtCreate(new Date());
        msg.setBizType("ORDER");
        msg.setMessageType("CREATE_SUCCESS");
        msg.setIdentifier(order.getId().toString());
        msg.setContent(order.getUserId().toString());
        msg.setState("INIT");
        tbCreateOrderMessageMapper.insert(msg);

        return msg;
    }


    public TbOrder insertOrder() {
        TbOrder tbOrder = new TbOrder();
        tbOrder.setUserId(1L);
        tbOrder.setGmtCreate(new Date());
        tbOrderMapper.insert(tbOrder);

        return tbOrder;
    }


}
