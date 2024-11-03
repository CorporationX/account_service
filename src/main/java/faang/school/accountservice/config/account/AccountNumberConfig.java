package faang.school.accountservice.config.account;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "account.numbers.batch-size")
public class AccountNumberConfig {

    private int batchSize;
}