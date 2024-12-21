package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisMessage;
import faang.school.accountservice.dto.PaymentOperationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic payment_service_initiate;

    public void publishResponse(String correlationId, PaymentOperationDto payload) {
        log.info("Publishing response message with correlationId: {}", correlationId);

        try {
            RedisMessage responseMessage = new RedisMessage();
            responseMessage.setCorrelationId(correlationId);
            responseMessage.setType("RESPONSE");
            responseMessage.setPayload(payload);

            redisTemplate.convertAndSend(payment_service_initiate.getTopic(), responseMessage);
            log.debug("Successfully published response message. CorrelationId: {}, Payload: {}",
                    correlationId, payload);
        } catch (Exception e) {
            log.error("Failed to publish response message. CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage(), e);
            throw e;
        }
    }

    public void publishError(String correlationId, String errorMessage) {
        log.info("Publishing error message with correlationId: {}", correlationId);

        try {
            RedisMessage errorResponse = new RedisMessage();
            errorResponse.setCorrelationId(correlationId);
            errorResponse.setType("RESPONSE");
            errorResponse.setError(errorMessage);

            redisTemplate.convertAndSend(payment_service_initiate.getTopic(), errorResponse);
            log.debug("Successfully published error message. CorrelationId: {}, Error: {}",
                    correlationId, errorMessage);
        } catch (Exception e) {
            log.error("Failed to publish error message. CorrelationId: {}, Original error: {}, Publishing error: {}",
                    correlationId, errorMessage, e.getMessage(), e);
            throw e;
        }
    }
}