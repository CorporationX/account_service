package faang.school.accountservice.listener.transfer;

import faang.school.accountservice.dto.event.request.ClearRequestEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.publisher.BrokenTransferEventPublisher;
import faang.school.accountservice.publisher.transfer.failed.ClearFailedPublisher;
import faang.school.accountservice.publisher.transfer.success.ClearSuccessPublisher;
import faang.school.accountservice.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearRequestListener extends AbstractTransferEventListener<ClearRequestEvent> {

    private final TransferService transferService;
    private final ClearSuccessPublisher clearSuccessPublisher;
    private final ClearFailedPublisher clearFailedPublisher;

    public ClearRequestListener(
            BrokenTransferEventPublisher brokenTransferEventPublisher,
            TransferService transferService,
            ClearSuccessPublisher clearSuccessPublisher,
            ClearFailedPublisher clearFailedPublisher,
            @Value(value = "${spring.kafka.topics.transfer.consume.cancellation-requests-topic.name}")
            String topic
    ) {
        super(topic, brokenTransferEventPublisher);
        this.transferService = transferService;
        this.clearSuccessPublisher = clearSuccessPublisher;
        this.clearFailedPublisher = clearFailedPublisher;
    }


    @KafkaListener(
            topics = "${spring.kafka.topics.transfer.consume.clear-requests-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaClearEventListener"
    )
    public void handleClear(ClearRequestEvent event) {
        handleEventWithValidation(event);
    }

    @Override
    public void handle(ClearRequestEvent event) {
        try {
            BalanceTransfer balanceTransfer = transferService.clearTransfer(event);
            clearSuccessPublisher.publishSuccessfulClearEvent(balanceTransfer);
        } catch (TransferException exception) {
            clearFailedPublisher.publishClearFailedEvent(event, exception);
        }
    }

    @Override
    public boolean isEventValid(ClearRequestEvent event) {
        return validateObjectNonNullData(
                event,
                event::getTransactionId,
                event::getInitiator
        );
    }
}