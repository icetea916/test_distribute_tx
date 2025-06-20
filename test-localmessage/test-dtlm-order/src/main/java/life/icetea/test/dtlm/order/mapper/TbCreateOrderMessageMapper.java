package life.icetea.test.dtlm.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.icetea.test.dtlm.order.domain.entity.TbCreateOrderMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface TbCreateOrderMessageMapper extends BaseMapper<TbCreateOrderMessage> {

    @Update("UPDATE tb_create_order_message SET state='PUBLISHED',rq_msg_id=#{rqMsgId} WHERE id=#{msgId}")
    int updateStateToPublished(@Param("msgId") Long msgId, @Param("rqMsgId") String rqMsgId);

    @Update("UPDATE tb_create_order_message SET state='FAILED', fail_reason=#{reason} WHERE id=#{msgId}")
    int updateStateToFailed(@Param("msgId") Long msgId, @Param("reason") String failReason);

    @Update("UPDATE tb_create_order_message SET state='FINISHED' WHERE id=#{msgId}")
    int updateStateToFinished(@Param("msgId") Long msgId);
}
