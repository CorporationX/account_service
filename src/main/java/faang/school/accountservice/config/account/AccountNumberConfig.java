package faang.school.accountservice.config.account;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "account.numbers.minimum-numbers")
public class AccountNumberConfig {
    private int checkingIndividual;
}