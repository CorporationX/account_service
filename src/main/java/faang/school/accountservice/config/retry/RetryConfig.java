package faang.school.accountservice.config.retry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfig {

    @Bean
    public int maxAttempts(@Value("${retry.maxAttempts}") int maxAttempts) {
        return maxAttempts;
    }

    @Bean
    public long backoffDelay(@Value("${retry.backoff.delay}") long backoffDelay) {
        return backoffDelay;
    }

    @Bean
    public double backoffMultiplier(@Value("${retry.backoff.multiplier}") double backoffMultiplier) {
        return backoffMultiplier;
    }
}
