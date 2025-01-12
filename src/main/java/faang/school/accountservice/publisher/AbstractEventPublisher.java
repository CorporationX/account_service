package faang.school.accountservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements MessagePublisher<T>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(T event) {
        log.info("sending message to redis");
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
    public abstract Class<T> getInstance();
}
