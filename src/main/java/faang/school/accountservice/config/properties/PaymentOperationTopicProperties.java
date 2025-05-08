package faang.school.accountservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topics.payment-operation")
public record PaymentOperationTopicProperties(
        String name,
        int partitions,
        int replicas
) {}
