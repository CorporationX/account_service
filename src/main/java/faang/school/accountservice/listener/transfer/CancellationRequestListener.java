package faang.school.accountservice.listener.transfer;

import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.publisher.BrokenTransferEventPublisher;
import faang.school.accountservice.publisher.transfer.failed.CancellationFailedPublisher;
import faang.school.accountservice.publisher.transfer.success.CancellationSuccessPublisher;
import faang.school.accountservice.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancellationRequestListener extends AbstractTransferEventListener<CancellationRequestEvent> {

    private final TransferService transferService;
    private final CancellationSuccessPublisher cancellationSuccessPublisher;
    private final CancellationFailedPublisher cancellationFailedPublisher;

    public CancellationRequestListener(
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            TransferService transferService,
            CancellationSuccessPublisher cancellationSuccessPublisher,
            CancellationFailedPublisher cancellationFailedPublisher,
            @Value(value = "${spring.kafka.topics.transfer.consume.cancellation-requests-topic.name}")
            String topic) {
        super(topic, brokenTransferEventPublisher);
        this.transferService = transferService;
        this.cancellationSuccessPublisher = cancellationSuccessPublisher;
        this.cancellationFailedPublisher = cancellationFailedPublisher;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.cancellation-requests-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaCancellationEventListener"
    )
    public void handleCancellation(CancellationRequestEvent event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(CancellationRequestEvent event) {
        try {
            BalanceTransfer balanceTransfer = transferService.cancelAuthorizationTransfer(event);
            cancellationSuccessPublisher.publishSuccessfulCancellation(balanceTransfer, event);
        } catch (TransferException exception) {
            cancellationFailedPublisher.publishCancellationFailedEvent(event, exception);
        }
    }

    @Override
    public boolean isEventValid(CancellationRequestEvent event) {
        return validateObjectNonNullData(
                event,
                event::getTransactionId,
                event::getUserId
        );
    }
}