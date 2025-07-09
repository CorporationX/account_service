package faang.school.accountservice.listener.transfer;

import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.publisher.BrokenTransferEventPublisher;
import faang.school.accountservice.publisher.transfer.failed.AuthorizationFailedPublisher;
import faang.school.accountservice.publisher.transfer.success.AuthorizationSuccessPublisher;
import faang.school.accountservice.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationRequestListener extends AbstractTransferEventListener<AuthorizationRequestEvent> {

    private final TransferService transferService;
    private final AuthorizationSuccessPublisher authorizationSuccessPublisher;
    private final AuthorizationFailedPublisher authorizationFailedPublisher;

    public AuthorizationRequestListener(
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            TransferService transferService,
            AuthorizationSuccessPublisher authorizationSuccessPublisher,
            AuthorizationFailedPublisher authorizationFailedPublisher,
            @Value(value = "${spring.kafka.topics.transfer.consume.authorization-requests-topic.name}")
            String topic
    ) {
        super(
                topic,
                brokenTransferEventPublisher
        );
        this.transferService = transferService;
        this.authorizationSuccessPublisher = authorizationSuccessPublisher;
        this.authorizationFailedPublisher = authorizationFailedPublisher;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.authorization-requests-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaAuthorizationEventListener"
    )
    public void handleAuthorized(AuthorizationRequestEvent event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(AuthorizationRequestEvent event) {
        BalanceTransfer newBalanceTransfer = null;
        try {
            newBalanceTransfer = transferService.authorizeTransfer(event);
            authorizationSuccessPublisher.publishSuccessfulAuthorization(newBalanceTransfer, event);
        } catch (TransferException exception) {
            authorizationFailedPublisher.publishFailedAuthorization(newBalanceTransfer, event, exception);
        }
    }

    @Override
    public boolean isEventValid(AuthorizationRequestEvent event) {
        return validateObjectNonNullData(
                event,
                event::getAuthorizationId,
                event::getUserId,
                event::getSourceId,
                event::getTargetId
        );
    }
}