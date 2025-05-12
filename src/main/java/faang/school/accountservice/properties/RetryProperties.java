package faang.school.accountservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "retry")
public class RetryProperties {
    private Integer maxAttempts;
    private Long delay;
}