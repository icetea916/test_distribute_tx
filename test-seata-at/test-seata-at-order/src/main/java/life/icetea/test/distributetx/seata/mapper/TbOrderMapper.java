package life.icetea.test.distributetx.seata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.icetea.test.distributetx.seata.domain.entity.TbOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbOrderMapper extends BaseMapper<TbOrder> {

}
