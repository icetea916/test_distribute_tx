package test.icetea.txmsg.userpoint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import test.icetea.txmsg.userpoint.domain.entity.TbUserPoint;

public interface TbUserPointMapper extends BaseMapper<TbUserPoint> {

    int addUserPoint(Long userId);

}
