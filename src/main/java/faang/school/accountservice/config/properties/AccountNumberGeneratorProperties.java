package faang.school.accountservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "account-number-generator")
@Getter
@Setter
public class AccountNumberGeneratorProperties {
    private int maxGenerationAttempts = 100;
}

