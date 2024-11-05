package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestStatusPublisher implements EventPublisher<RequestDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RequestDto event) {
        String channel = redisProperties.getChannels().get("request_status_notification");
        redisTemplate.convertAndSend(channel, event);
    }
}
