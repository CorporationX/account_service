package faang.school.accountservice.listener;

import faang.school.accountservice.dto.message.AuthorizationMessage;
import faang.school.accountservice.dto.message.CancellationMessage;
import faang.school.accountservice.dto.message.ClearingMessage;
import faang.school.accountservice.service.AccountOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageListeners {
    private final AccountOperationService accountOperationService;

    @KafkaListener(topics = "${spring.kafka.topics.authorization}" )
    public void authorizationMessageListener(@Payload @Valid AuthorizationMessage message) {
        accountOperationService.processAuthorization(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.cancellation}" )
    public void cancellationMessageListener(@Payload @Valid CancellationMessage message) {
        accountOperationService.processCancellation(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.clearing}" )
    public void clearingMessageListener(@Payload @Valid ClearingMessage message) {
        accountOperationService.processClearing(message);
    }
}
