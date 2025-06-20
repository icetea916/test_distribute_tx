package life.icetea.test.dtlm.order.controller;

import cn.hutool.core.map.MapUtil;
import life.icetea.test.dtlm.order.mapper.TbCreateOrderMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("create_order")
@Slf4j
public class CreateOrderMsgController {

    @Autowired
    public TbCreateOrderMessageMapper tbCreateOrderMessageMapper;

    @RequestMapping("finished")
    public Map<String, Object> finished(@NotNull Long msgId) {
        int i = tbCreateOrderMessageMapper.updateStateToFinished(msgId);

        Map<String, Object> result = MapUtil.<String, Object>builder()
                .put("success", i > 0)
                .build();
        return result;
    }

}
