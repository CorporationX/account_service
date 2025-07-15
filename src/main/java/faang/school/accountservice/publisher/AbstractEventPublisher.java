package faang.school.accountservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    private final String topic;
    private final KafkaTemplate<String, T> kafkaTemplate;

    public void publish(T event) {
        kafkaTemplate.send(topic, event)
                .thenAccept(result -> {
                    log.info("Message is sent: {}", result.getRecordMetadata());
                })
                .exceptionally(e -> {
                    log.error("Failed to send message: {}", e.getMessage(), e);
                    if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                        log.error("Topic not exist: {}", topic);
                    }
                    return null;
                });
    }
}