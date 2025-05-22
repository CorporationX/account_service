package faang.school.accountservice.publisher;

import faang.school.accountservice.events.RequestEventEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestEventsPublisher {

    @Qualifier("requestEventsRedisTemplate")
    private final RedisTemplate<String, RequestEventEvent> redisTemplate;
    @Qualifier("requestEventsTopic")
    private final ChannelTopic topic;

    @Retryable(
            retryFor = {RedisConnectionFailureException.class, SerializationException.class},
            maxAttemptsExpression = "${spring.data.redis.retry.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${spring.data.redis.retry.backoff-delay}",
                    multiplierExpression = "${spring.data.redis.retry.backoff-multiplier}"
            ))
    public void publish(RequestEventEvent requestEventEvent) {
        redisTemplate.convertAndSend(topic.getTopic(), requestEventEvent);
    }
}
