package faang.school.accountservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topics.payment-response")
public record PaymentResponseTopicProperties(
        String name,
        int partitions,
        int replicas
) {}
