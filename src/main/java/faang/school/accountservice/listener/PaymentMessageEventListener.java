package faang.school.accountservice.listener;

import faang.school.accountservice.config.redis.RedisMessage;
import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.service.payment.PaymentOperationStrategy;
import faang.school.accountservice.service.payment.PaymentOperationStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageEventListener implements MessageListener {
    private final PaymentOperationStrategyFactory strategyFactory;

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
                log.info("Payment request details - Type: {}, Status: {}, Amount: {}, Currency: {}, Owner: {}, Recipient: {}",
                        payload.getOperationType(),
                        payload.getStatus(),
                        payload.getAmount(),
                        payload.getCurrency(),
                        payload.getOwnerAccId(),
                        payload.getRecipientAccId());

                String correlationId = receivedMessage.getCorrelationId();
                PaymentOperationStrategy strategy = strategyFactory.getStrategy(correlationId, payload.getOperationType());
                strategy.process(payload, correlationId);

                log.info("Successfully processed payment request. CorrelationId: {}",
                        receivedMessage.getCorrelationId());

            } catch (Exception e) {
                log.error("Error processing payment request. CorrelationId: {}",
                        receivedMessage.getCorrelationId(), e);
            }
        }
    }
}