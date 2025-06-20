package life.icetea.test.distributetx.seata.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import io.seata.spring.annotation.GlobalTransactional;
import life.icetea.test.distributetx.seata.domain.entity.TbOrder;
import life.icetea.test.distributetx.seata.mapper.TbOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("create")
    @GlobalTransactional
    public Map<String, Object> createOrder(@RequestParam("isTest") Boolean isTest) {
        TbOrder order = null;
        // 创建订单
        log.info("创建订单开始");
        order = insertOrder();
        // 模拟失败
        if (isTest) {
            int i = 1 / 0;
        }
        log.info("添加积分请求");
        addUserPoint(order);


        Map<String, Object> result = MapUtil.<String, Object>builder()
                .put("order", JSONUtil.toJsonStr(order))
                .put("success", true)
                .build();

        return result;
    }

    private void addUserPoint(TbOrder order) {
        Long userId = order.getUserId();
        String url = "http://localhost:8081/user_point/add?userId=" + userId;
        String resp = restTemplate.getForObject(url, String.class);
        log.info("添加积分成功: resp={}", resp);
    }


    public TbOrder insertOrder() {
        TbOrder tbOrder = new TbOrder();
        tbOrder.setUserId(1L);
        tbOrder.setGmtCreate(new Date());
        tbOrderMapper.insert(tbOrder);

        return tbOrder;
    }

}
