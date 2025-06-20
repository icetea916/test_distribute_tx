package test.icetea.txmsg.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import test.icetea.txmsg.order.domain.entity.TbOrder;

@Mapper
public interface TbOrderMapper extends BaseMapper<TbOrder> {

}
