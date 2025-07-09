package faang.school.accountservice.config.kafka.topics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topic.payment-authorization-request")
@Configuration
public class KafkaPaymentAuthorizationReqTopicProperties {
    private String name;
    private int partitions;
}
