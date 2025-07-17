package faang.school.accountservice.publisher.transfer.success;

import faang.school.accountservice.dto.event.responce.success.ClearSuccessEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClearSuccessPublisher extends AbstractEventPublisher<ClearSuccessEvent> {
    public ClearSuccessPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.clear-success-topic.name}") String topic,
            KafkaTemplate<String, ClearSuccessEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }

    public void publishSuccessfulClearEvent(BalanceTransfer balanceTransfer) {
        ClearSuccessEvent clearSuccessEvent = ClearSuccessEvent.builder()
                .transactionId(balanceTransfer.getId())
                .description("Transaction %s cleared successfully".formatted(balanceTransfer.getId()))
                .build();

        publish(clearSuccessEvent);
    }
}