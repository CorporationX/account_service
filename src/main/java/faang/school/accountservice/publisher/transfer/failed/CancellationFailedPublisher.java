package faang.school.accountservice.publisher.transfer.failed;

import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.dto.event.responce.failed.CancellationFailedEvent;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CancellationFailedPublisher extends AbstractEventPublisher<CancellationFailedEvent> {
    public CancellationFailedPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.cancellation-failed-topic.name}") String topic,
            KafkaTemplate<String, CancellationFailedEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }

    public void publishCancellationFailedEvent(CancellationRequestEvent event, TransferException exception) {
        CancellationFailedEvent cancellationFailedEvent = CancellationFailedEvent.builder()
                .transactionId(event.getTransactionId())
                .description(exception.getKafkaMessage())
                .build();

        publish(cancellationFailedEvent);
    }
}