package life.icetea.test.distributetx.seata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@MapperScan("life.icetea.test.distributetx.seata.mapper")
@Configuration
public class SeataATOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataATOrderApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 设置连接超时时间为5秒
        factory.setReadTimeout(5000); // 设置读取超时时间为5秒
        factory.setReadTimeout(30000); // 设置读取超时时间为30秒

        return factory;
    }
}