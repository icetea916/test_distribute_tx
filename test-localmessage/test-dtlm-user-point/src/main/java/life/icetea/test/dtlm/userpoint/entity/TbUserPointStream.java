package life.icetea.test.dtlm.userpoint.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_user_point_stream")
public class TbUserPointStream {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String identifier;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private UserPointStreamRecord record;

    private Date gmtCreate;


    public static class UserPointStreamRecord {

        private Long from;

        private Long to;

        @Override
        public String toString() {
            return JSONUtil.toJsonStr(this);
        }

        public Long getFrom() {
            return from;
        }

        public void setFrom(Long from) {
            this.from = from;
        }

        public Long getTo() {
            return to;
        }

        public void setTo(Long to) {
            this.to = to;
        }
    }

}
