package life.icetea.test.distributetx.seata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.icetea.test.distributetx.seata.domain.entity.TbUserPoint;

public interface TbUserPointMapper extends BaseMapper<TbUserPoint> {

    int addUserPoint(Long userId);

}
