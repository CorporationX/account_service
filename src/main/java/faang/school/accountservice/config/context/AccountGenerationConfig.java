package faang.school.accountservice.config.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "account.generation")
public class AccountGenerationConfig {

    private int maxRetryAttempts = 5;
    private int defaultBatchSize = 100;
    private int maxDuplicateRetries = 3;
    private int minPoolSize = 50;
}