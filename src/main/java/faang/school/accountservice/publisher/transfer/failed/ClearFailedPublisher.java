package faang.school.accountservice.publisher.transfer.failed;

import faang.school.accountservice.dto.event.request.ClearRequestEvent;
import faang.school.accountservice.dto.event.responce.failed.ClearFailedEvent;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClearFailedPublisher extends AbstractEventPublisher<ClearFailedEvent> {
    public ClearFailedPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.clear-failed-topic.name}") String topic,
            KafkaTemplate<String, ClearFailedEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }

    public void publishClearFailedEvent(ClearRequestEvent event, TransferException exception) {
        ClearFailedEvent clearFailedEvent = ClearFailedEvent.builder()
                .transactionId(event.getTransactionId())
                .description(exception.getKafkaMessage())
                .build();

        publish(clearFailedEvent);
    }
}