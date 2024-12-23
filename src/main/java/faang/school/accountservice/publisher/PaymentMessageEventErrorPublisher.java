package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageEventErrorPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic initiateResChannelTopic;

    public void publishError(String correlationId, String errorMessage) {
        log.info("Publishing error message with correlationId: {}", correlationId);

        try {
            RedisMessage errorResponse = new RedisMessage();
            errorResponse.setCorrelationId(correlationId);
            errorResponse.setType("RESPONSE");
            errorResponse.setError(errorMessage);

            redisTemplate.convertAndSend(initiateResChannelTopic.getTopic(), errorResponse);
            log.debug("Successfully published error message. CorrelationId: {}, Error: {}",
                    correlationId, errorMessage);
        } catch (Exception e) {
            log.error("Failed to publish error message. CorrelationId: {}, Original error: {}, Publishing error: {}",
                    correlationId, errorMessage, e.getMessage(), e);
            throw e;
        }
    }
}