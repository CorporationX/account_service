package faang.school.accountservice.config.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaDeadLettersConfig {

    @Value(value = "${spring.kafka.topics.dead-letters.transfer-dead-letters-topic.name}")
    private String transferDeadLettersTopic;
    @Value(value = "${spring.kafka.topics.dead-letters.transfer-dead-letters-topic.default-interval-millis}")
    private long defaultInterval;
    @Value(value = "${spring.kafka.topics.dead-letters.transfer-dead-letters-topic.default-attempts}")
    private long defaultAttempts;

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublisher(KafkaTemplate<String, String> template) {
        return new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(transferDeadLettersTopic, record.partition()));
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublisher) {
        return new DefaultErrorHandler(deadLetterPublisher, new FixedBackOff(defaultInterval, defaultAttempts));
    }
}