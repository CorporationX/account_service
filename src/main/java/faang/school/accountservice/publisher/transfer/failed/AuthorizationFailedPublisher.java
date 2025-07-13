package faang.school.accountservice.publisher.transfer.failed;

import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.dto.event.responce.failed.AuthorizationFailedEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthorizationFailedPublisher extends AbstractEventPublisher<AuthorizationFailedEvent> {
    public AuthorizationFailedPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.authorization-failed-topic.name}") String topic,
            KafkaTemplate<String, AuthorizationFailedEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }

    public void publishFailedAuthorization(
            BalanceTransfer balanceTransfer,
            AuthorizationRequestEvent event,
            TransferException transferException
    ) {
        UUID balanceTransferId = balanceTransfer != null ? balanceTransfer.getId() : null;
        AuthorizationFailedEvent authorizationFailedEvent = AuthorizationFailedEvent.builder()
                .transactionId(balanceTransferId)
                .authorizationId(event.getAuthorizationId())
                .description(transferException.getKafkaMessage())
                .build();

        publish(authorizationFailedEvent);
    }
}