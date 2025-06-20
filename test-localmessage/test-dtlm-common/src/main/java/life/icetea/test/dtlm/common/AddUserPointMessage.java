package life.icetea.test.dtlm.common;

import lombok.Data;

@Data
public class AddUserPointMessage {

    private Long userId;

    private Long msgId;

    private Long orderId;

}
