package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestStatusPublisher implements EventPublisher<RequestDto> {

    @Value("${spring.data.redis.channels.request-status}")
    private final String channel;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RequestDto event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
