package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topic.success-payment-authorization-response")
@Configuration
public class KafkaSuccessPaymentAuthorizationResTopicProperties {
    private String name;
    private int partitions;
}
