package test.icetea.txmsg.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserPointMessage {

    private Long userId;

    /**
     * 使用事务消息，则需提前发送订单信息，所以自增不能使用，这里用自定义order_no
     */
    private String orderNo;

}
