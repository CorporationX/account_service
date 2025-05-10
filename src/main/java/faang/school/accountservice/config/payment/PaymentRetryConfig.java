package faang.school.accountservice.config.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry.payment")
@Validated
public class PaymentRetryConfig {

    @NotNull
    private Integer maxAttempts;

    @NotNull
    private long initialDelay;

    @NotNull
    private Double multiplier;
}
