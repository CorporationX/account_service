package faang.school.accountservice.publisher.transfer.success;

import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.dto.event.responce.success.AuthorizationSuccessEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationSuccessPublisher extends AbstractEventPublisher<AuthorizationSuccessEvent> {
    public AuthorizationSuccessPublisher(
            @Value(value = "${spring.kafka.topics.transfer.publish.authorization-success-topic.name}") String topic,
            KafkaTemplate<String, AuthorizationSuccessEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }

    public void publishSuccessfulAuthorization(BalanceTransfer transfer, AuthorizationRequestEvent event) {
        AuthorizationSuccessEvent authorizationSuccessEvent = AuthorizationSuccessEvent.builder()
                .transactionId(transfer.getId())
                .authorizationId(event.getAuthorizationId())
                .description("Transaction %s authorized successfully".formatted(event.getAuthorizationId()))
                .build();
        publish(authorizationSuccessEvent);
    }
}