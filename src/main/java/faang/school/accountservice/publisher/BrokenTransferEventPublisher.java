package faang.school.accountservice.publisher;

import faang.school.accountservice.dto.deadletter.DeadLetterMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BrokenTransferEventPublisher extends AbstractEventPublisher<DeadLetterMessage> {
    public BrokenTransferEventPublisher(
            @Value(value = "${spring.kafka.topics.dead-letters.transfer-dead-letters-topic.name}") String topic,
            KafkaTemplate<String, DeadLetterMessage> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }

    public void publish(String topic, Object payload) {
        DeadLetterMessage dltMessage = DeadLetterMessage.builder()
                .originalTopic(topic)
                .originalPayload(payload)
                .errorMessage("Invalid transfer event object!")
                .failedAt(LocalDateTime.now())
                .build();
        super.publish(dltMessage);
    }
}
