package faang.school.accountservice.config.retry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Configuration
@EnableRetry
public class RetryConfig {

    @Value("${retry.interestCalculator.maxAttempts}")
    private int maxAttempts;

    @Bean
    public SimpleRetryPolicy retryPolicy() {
        return new SimpleRetryPolicy(maxAttempts);
    }
}
