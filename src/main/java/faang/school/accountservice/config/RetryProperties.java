package faang.school.accountservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.retry")
@Component
@Data
public class RetryProperties {
    private int maxAttempts;
    private long delay;
}