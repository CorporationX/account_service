package faang.school.accountservice.listener;

import faang.school.accountservice.config.redis.RedisMessage;
import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentStatus;
import faang.school.accountservice.dto.PaymentValidationResult;
import faang.school.accountservice.enums.AccValidationStatus;
import faang.school.accountservice.publisher.PaymentMessageEventPublisher;
import faang.school.accountservice.service.account.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageEventListener implements MessageListener {
    private final PaymentMessageEventPublisher paymentMessageEventPublisher;
    private final AccountService accountService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received new Redis message. Channel: {}", new String(message.getChannel()));

        Jackson2JsonRedisSerializer<RedisMessage> serializer =
                new Jackson2JsonRedisSerializer<>(RedisMessage.class);

        RedisMessage receivedMessage;
        try {
            receivedMessage = serializer.deserialize(message.getBody());
            log.info("Successfully deserialized message. CorrelationId: {}, Type: {}",
                    receivedMessage.getCorrelationId(), receivedMessage.getType());
        } catch (Exception e) {
            log.error("Failed to deserialize Redis message", e);
            return;
        }

        if ("REQUEST".equals(receivedMessage.getType())) {
            log.info("Processing REQUEST message. CorrelationId: {}", receivedMessage.getCorrelationId());
            try {
                PaymentOperationDto payload = receivedMessage.getPayload();
                log.info("Payment request details - Amount: {}, Currency: {}, Owner: {}, Recipient: {}",
                        payload.getAmount(),
                        payload.getCurrency(),
                        payload.getOwnerAccId(),
                        payload.getRecipientAccId());

                PaymentOperationDto result = processPaymentBaseRequest(receivedMessage.getPayload());
                log.info("Successfully processed payment request. CorrelationId: {}, PaymentId: {}",
                        receivedMessage.getCorrelationId(), result.getId());

                PaymentValidationResult validationResult = validateAccounts(payload);
                switch (validationResult.status()) {
                    case SUCCESS: {
                        result.setStatus(PaymentStatus.AUTHORIZED);
                        paymentMessageEventPublisher.publishResponse(
                                receivedMessage.getCorrelationId(),
                                result
                        );
                    }
                    case ERROR: {
                        result.setStatus(PaymentStatus.FAILED);
                        paymentMessageEventPublisher.publishResponse(
                                receivedMessage.getCorrelationId(),
                                result
                        );
                    }
                }
                log.info("Published success response. CorrelationId: {}", receivedMessage.getCorrelationId());
            } catch (Exception e) {
                log.error("Error processing payment request. CorrelationId: {}, Error: {}",
                        receivedMessage.getCorrelationId(), e.getMessage(), e);
            }
        } else {
            log.warn("Received message with unexpected type: {}. CorrelationId: {}",
                    receivedMessage.getType(), receivedMessage.getCorrelationId());
        }
    }

    private PaymentOperationDto processPaymentBaseRequest(PaymentOperationDto payload) {
        log.info("Processing payment operation. PaymentId: {}", payload.getId());

        LocalDateTime clearScheduledAt = LocalDateTime.now().plusMinutes(3L);
        LocalDateTime updatedAt = LocalDateTime.now();

        PaymentOperationDto result = PaymentOperationDto.builder()
                .id(payload.getId())
                .amount(payload.getAmount())
                .currency(payload.getCurrency())
                .ownerAccId(payload.getOwnerAccId())
                .recipientAccId(payload.getRecipientAccId())
                .createdAt(payload.getCreatedAt())
                .updatedAt(updatedAt.toString())
                .operationType(payload.getOperationType())
                .clearScheduledAt(clearScheduledAt.toString())
                .createdAt(payload.getCreatedAt())
                .updatedAt(updatedAt.toString())
                .build();

        log.info("Payment processing completed. PaymentId: {}, Status: {}, ClearScheduledAt: {}",
                result.getId(), result.getStatus(), result.getClearScheduledAt());
        return result;
    }

    public PaymentValidationResult validateAccounts(PaymentOperationDto payload) {
        try {
            if (!accountService.existsAccById(payload.getOwnerAccId())) {
                return new PaymentValidationResult(
                        AccValidationStatus.ERROR,
                        new EntityNotFoundException("Owner account not found: " + payload.getOwnerAccId())
                );
            }
            if (!accountService.existsAccById(payload.getRecipientAccId())) {
                return new PaymentValidationResult(
                        AccValidationStatus.ERROR,
                        new EntityNotFoundException("Recipient account not found: " + payload.getRecipientAccId())
                );
            }
            return new PaymentValidationResult(AccValidationStatus.SUCCESS, null);
        } catch (Exception e) {
            return new PaymentValidationResult(AccValidationStatus.ERROR, e);
        }
    }
}