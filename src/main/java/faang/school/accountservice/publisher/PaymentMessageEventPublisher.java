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
public class PaymentMessageEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic initiateResChannelTopic;

    public void publishResponse(String correlationId, PaymentOperationDto payload) {
        log.info("Publishing response message with correlationId: {}", correlationId);
        RedisMessage responseMessage = new RedisMessage();
        try {
            responseMessage.setCorrelationId(correlationId);
            responseMessage.setType("RESPONSE");
            responseMessage.setPayload(payload);

            redisTemplate.convertAndSend(initiateResChannelTopic.getTopic(), responseMessage);
            log.debug("Successfully published response message. CorrelationId: {}, Payload: {}",
                    correlationId, payload);
        } catch (Exception e) {
            log.error("Failed to publish response message. CorrelationId: {}, Error: {}",
                    correlationId, e.getMessage(), e);
            throw e;
        }
    }
}