package faang.school.accountservice;

import faang.school.accountservice.scheduler.AccountNumberTaskProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients("faang.school.accountservice.client")
@EnableConfigurationProperties(AccountNumberTaskProperties.class)
public class AccountServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AccountServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
