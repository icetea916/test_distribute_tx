package life.icetea.test.distributetx.seata.controller;

import io.seata.core.exception.RmTransactionException;
import life.icetea.test.distributetx.seata.domain.entity.TbUserPoint;
import life.icetea.test.distributetx.seata.mapper.TbUserPointMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("user_point")
@Slf4j
public class UserPointController {

    @Autowired
    private TbUserPointMapper tbUserPointMapper;


    @RequestMapping("add")
    public String add(@RequestParam("userId") Long userId) throws InterruptedException {
        // 模拟失败
//        int i = 1 / 0;
        // 模拟请求超时
        TimeUnit.SECONDS.sleep(10);
        // 增加积分，插入积分流水 2步都在一个事务中
        TbUserPoint userPoint = addUserPoint(userId);
        log.info("增加用户积分成功，userId={}", userId);

        return "success";
    }

    private TbUserPoint addUserPoint(Long userId) {
        tbUserPointMapper.addUserPoint(userId);

        return tbUserPointMapper.selectById(userId);
    }

    @ExceptionHandler
    public String exceptionHandler(RmTransactionException e) {
        log.error("全局事务异常处理", e);

        return "error";
    }

}
