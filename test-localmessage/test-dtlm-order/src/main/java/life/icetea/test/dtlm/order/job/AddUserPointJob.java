package life.icetea.test.dtlm.order.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import life.icetea.test.dtlm.common.AddUserPointMessage;
import life.icetea.test.dtlm.order.domain.entity.TbCreateOrderMessage;
import life.icetea.test.dtlm.order.mapper.TbCreateOrderMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddUserPointJob {

    @Autowired
    private TbCreateOrderMessageMapper tbCreateOrderMessageMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${test.create-order-topic}")
    private String topicName;

    @Scheduled(cron = "0/5 * * * * ?")
    public void run() {
        // 查询
        Page<TbCreateOrderMessage> page = ChainWrappers.lambdaQueryChain(tbCreateOrderMessageMapper)
                .eq(TbCreateOrderMessage::getState, "FAILED")
                .lt(TbCreateOrderMessage::getRetryCount, 6) // 最多重试6次
                .page(Page.of(1, 100));

        if (CollUtil.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(record -> {
                Long msgId = record.getId();
                Long userId = record.getUserId();
                Long orderId = Long.valueOf(record.getIdentifier());
                try {
                    // 重新发送增加用户积分消息, consumer幂等
//                int i = 1 / 0;
                    AddUserPointMessage addUserPointMessage = new AddUserPointMessage();
                    addUserPointMessage.setMsgId(msgId);
                    addUserPointMessage.setUserId(userId);
                    SendResult sendResult = rocketMQTemplate.syncSend(topicName, addUserPointMessage);
                    String rqMsgId = sendResult.getMsgId();
                    log.info("发送消息成功，orderId={}, rqMsgId={}", orderId, sendResult.getMsgId());
                    // 修改msg状态为已发送,若失败定时任务重试
//                    int i = 1 / 0;
                    tbCreateOrderMessageMapper.updateStateToPublished(msgId, rqMsgId);
                    log.info("修改消息状态成功，orderId={}, msgId={}, rqMsgId={}", orderId, msgId, rqMsgId);
                } catch (Exception e) {
                    // 发送失败修改msg状态
                    log.error("发送消息异常, orderId={}, msgId={}", orderId, msgId, e);
                    ChainWrappers.lambdaUpdateChain(tbCreateOrderMessageMapper)
                            .setSql("retry_count = IFNULL( retry_count, 0 ) + 1 ")
                            // 刷新每次的失败原因
                            .set(TbCreateOrderMessage::getFailReason, ExceptionUtil.stacktraceToString(e))
                            .eq(TbCreateOrderMessage::getState, "FAILED")
                            .lt(TbCreateOrderMessage::getRetryCount, 6) // 最多重试6次
                            .update();
                }
            });
        }


    }

}
