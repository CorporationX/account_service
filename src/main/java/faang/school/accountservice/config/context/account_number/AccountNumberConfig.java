package faang.school.accountservice.config.context.account_number;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "account.number")
public class AccountNumberConfig {

    private String cron;
    private int batchSizeDefault;
    private Map<String, Integer> batchSize;
}