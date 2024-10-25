package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.PendingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmitPaymentPublisher implements EventPublisher<PendingDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(PendingDto event) {
        String channel = redisProperties.getChannels().get("result_progress_payment");
        redisTemplate.convertAndSend(channel, event);
    }
}

