package faang.school.accountservice.publisher.transfer.success;

import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.dto.event.responce.success.CancellationSuccessEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CancellationSuccessPublisher extends AbstractEventPublisher<CancellationSuccessEvent> {
    public CancellationSuccessPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.cancellation-success-topic.name}") String topic,
            KafkaTemplate<String, CancellationSuccessEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }

    public void publishSuccessfulCancellation(BalanceTransfer balanceTransfer, CancellationRequestEvent event) {
        CancellationSuccessEvent cancellationSuccessEvent = CancellationSuccessEvent.builder()
                .transactionId(event.getTransactionId())
                .description("Authorization for account %s for funds %s is canceled".formatted(
                        balanceTransfer.getSourceAccount().getId(),
                        balanceTransfer.getAmount()
                ))
                .build();
        publish(cancellationSuccessEvent);
    }
}