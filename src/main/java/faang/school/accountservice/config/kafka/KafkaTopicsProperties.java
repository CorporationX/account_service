package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties (prefix = "spring.kafka.topics")
@Configuration
public class KafkaTopicsProperties {
    private String dlqAuthPayment;
    private String authPaymentRequest;
    private String authPaymentResponse;
    private String authPaymentCancelRequest;
    private String authPaymentCancelResponse;
    private String authPaymentClearingRequest;
    private String authPaymentClearingResponse;
}
