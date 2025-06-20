package life.icetea.test.distributetx.seata;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("life.icetea.test.distributetx.seata.mapper")
public class SeataATInventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataATInventoryApplication.class, args);
    }

}