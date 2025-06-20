package life.icetea.test.dtlm.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_create_order_message")
public class TbCreateOrderMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String messageType;

    private String bizType;

    private String identifier;

    private String content;

    private String state;

    private Integer retryCount;

    private Long userId;

    private String failReason;
    /**
     * 响应消息ID
     */
    private String rqMsgId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

}
