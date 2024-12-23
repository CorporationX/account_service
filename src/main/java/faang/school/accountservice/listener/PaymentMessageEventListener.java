package faang.school.accountservice.listener;

import faang.school.accountservice.config.redis.RedisMessage;
import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentStatus;
import faang.school.accountservice.dto.PaymentValidationResult;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.enums.AccValidationStatus;
import faang.school.accountservice.publisher.PaymentMessageEventPublisher;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.validation.ValidationService;
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
    private final BalanceRepository balanceRepository;
    private final BalanceService balanceService;
    private final ValidationService validationService;

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

                PaymentOperationDto result = processPaymentBaseRequest(payload);
                log.info("Successfully processed payment request. CorrelationId: {}, PaymentId: {}",
                        receivedMessage.getCorrelationId(), result.getId());

                String correlationId = receivedMessage.getCorrelationId();

                PaymentValidationResult validationResult = validationService.validateAccounts(result);

                if (validationResult.status() == AccValidationStatus.SUCCESS) {
                    handleSuccessfulValidation(correlationId, result);
                } else {
                    handleFailedValidation(correlationId, result);
                }
            } catch (Exception e) {
                log.error("Error processing payment request. CorrelationId: {}",
                        receivedMessage.getCorrelationId(), e);
            }
        }
    }

    private void handleSuccessfulValidation(String correlationId, PaymentOperationDto payload) {
        payload.setStatus(PaymentStatus.AUTHORIZED);
        LocalDateTime clearScheduledAt = LocalDateTime.now().plusMinutes(3L);
        payload.setClearScheduledAt(clearScheduledAt.toString());

        PaymentDto initiatePaymentDto = PaymentDto.builder()
                .balanceId(balanceRepository.findByAccountId(payload.getOwnerAccId()))
                .paymentOperationType(payload.getOperationType())
                .value(payload.getAmount())
                .build();

        balanceService.update(payload.getOwnerAccId(), initiatePaymentDto);

        paymentMessageEventPublisher.publishResponse(
                correlationId,
                payload
        );
    }

    private void handleFailedValidation(String correlationId, PaymentOperationDto payload) {
        payload.setStatus(PaymentStatus.FAILED);
        paymentMessageEventPublisher.publishResponse(
                correlationId,
                payload
        );
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
}