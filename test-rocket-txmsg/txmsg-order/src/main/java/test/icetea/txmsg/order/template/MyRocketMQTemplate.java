package test.icetea.txmsg.order.template;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * 自定义template使用不同的produce发送消息
 */
@ExtRocketMQTemplateConfiguration(group = "my_txmsg_produce_group")
public class MyRocketMQTemplate extends RocketMQTemplate {

}
