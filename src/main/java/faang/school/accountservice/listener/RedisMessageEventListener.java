package faang.school.accountservice.listener;

import faang.school.accountservice.config.redis.RedisMessage;
import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentStatus;
import faang.school.accountservice.publisher.RedisMessagePublisher;
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
public class RedisMessageEventListener implements MessageListener {
    private final RedisMessagePublisher redisMessagePublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received new Redis message. Channel: {}", new String(message.getChannel()));

        Jackson2JsonRedisSerializer<RedisMessage> serializer =
                new Jackson2JsonRedisSerializer<>(RedisMessage.class);

        RedisMessage receivedMessage;
        try {
            receivedMessage = serializer.deserialize(message.getBody());
            log.debug("Successfully deserialized message. CorrelationId: {}, Type: {}",
                    receivedMessage.getCorrelationId(), receivedMessage.getType());
        } catch (Exception e) {
            log.error("Failed to deserialize Redis message", e);
            return;
        }

        if ("REQUEST".equals(receivedMessage.getType())) {
            log.info("Processing REQUEST message. CorrelationId: {}", receivedMessage.getCorrelationId());
            try {
                PaymentOperationDto payload = receivedMessage.getPayload();
                log.debug("Payment request details - Amount: {}, Currency: {}, Owner: {}, Recipient: {}",
                        payload.getAmount(),
                        payload.getCurrency(),
                        payload.getOwnerAccId(),
                        payload.getRecipientAccId());

                // Обработка запроса через сервис
                PaymentOperationDto result = processPaymentRequest(receivedMessage.getPayload());
                log.info("Successfully processed payment request. CorrelationId: {}, PaymentId: {}",
                        receivedMessage.getCorrelationId(), result.getId());

                // Публикация успешного ответа через publisher
                redisMessagePublisher.publishResponse(
                        receivedMessage.getCorrelationId(),
                        result
                );
                log.debug("Published success response. CorrelationId: {}", receivedMessage.getCorrelationId());
            } catch (Exception e) {
                log.error("Error processing payment request. CorrelationId: {}, Error: {}",
                        receivedMessage.getCorrelationId(), e.getMessage(), e);

                // Публикация ответа с ошибкой через publisher
                redisMessagePublisher.publishError(
                        receivedMessage.getCorrelationId(),
                        e.getMessage()
                );
                log.debug("Published error response. CorrelationId: {}", receivedMessage.getCorrelationId());
            }
        } else {
            log.warn("Received message with unexpected type: {}. CorrelationId: {}",
                    receivedMessage.getType(), receivedMessage.getCorrelationId());
        }
    }

    private PaymentOperationDto processPaymentRequest(PaymentOperationDto payload) {
        log.debug("Processing payment operation. PaymentId: {}", payload.getId());

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
                .status(PaymentStatus.AUTHORIZED)
                .operationType(payload.getOperationType())
                .clearScheduledAt(clearScheduledAt.toString())
                .createdAt(payload.getCreatedAt())
                .updatedAt(updatedAt.toString())
                .build();

        log.debug("Payment processing completed. PaymentId: {}, Status: {}, ClearScheduledAt: {}",
                result.getId(), result.getStatus(), result.getClearScheduledAt());

        return result;
    }
}