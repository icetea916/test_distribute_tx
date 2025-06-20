package life.icetea.test.dtlm.userpoint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("life.icetea.test.dtlm.userpoint.mapper")
public class UserPointApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserPointApplication.class);
    }
}