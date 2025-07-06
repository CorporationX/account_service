package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topic.failed-payment-authorization-response")
@Configuration
public class KafkaFailedPaymentAuthorizationResTopicProperties {
    private String name;
    private int partitions;
}
