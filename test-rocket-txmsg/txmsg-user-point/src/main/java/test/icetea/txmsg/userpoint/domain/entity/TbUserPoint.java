package test.icetea.txmsg.userpoint.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_user_point")
public class TbUserPoint {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private Long point;

    private Date gmtCreate;

    private Date gmtModified;

}
