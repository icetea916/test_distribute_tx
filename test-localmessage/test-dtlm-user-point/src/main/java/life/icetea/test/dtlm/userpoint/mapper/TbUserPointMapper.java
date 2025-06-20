package life.icetea.test.dtlm.userpoint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.icetea.test.dtlm.userpoint.entity.TbUserPoint;

public interface TbUserPointMapper extends BaseMapper<TbUserPoint> {

    int addUserPoint(Long userId);

}
