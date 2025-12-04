package faang.school.accountservice.service.notification.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestAndOperationProcessMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.requestAndOperations}")
    private String channel;

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
