package faang.school.accountservice;

import faang.school.accountservice.config.account.AccountGenerateConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients("faang.school.accountservice.client")
@EnableRetry
@EnableConfigurationProperties(AccountGenerateConfig.class)
public class AccountServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AccountServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
