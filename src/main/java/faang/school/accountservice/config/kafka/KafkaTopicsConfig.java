package faang.school.accountservice.config.kafka;

import faang.school.accountservice.config.properties.PaymentOperationTopicProperties;
import faang.school.accountservice.config.properties.PaymentResponseTopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicsConfig {

    private final PaymentOperationTopicProperties paymentOperationProperties;
    private final PaymentResponseTopicProperties paymentResponseProperties;

    @Bean
    public NewTopic paymentOperationTopic() {
        return createTopic(paymentOperationProperties.name(),
                paymentOperationProperties.partitions(),
                paymentOperationProperties.replicas());
    }

    @Bean
    public NewTopic paymentResponseTopic() {
        return createTopic(paymentResponseProperties.name(),
                paymentResponseProperties.partitions(),
                paymentResponseProperties.replicas());
    }

    private NewTopic createTopic(String name, int partitions, int replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

}
