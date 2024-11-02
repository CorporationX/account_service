package faang.school.accountservice.publisher.pending.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class PendingOperationToRedisPublisher implements PendingOperationStatusPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Topic checkingBalanceTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(CheckingAccountBalance checkingAccountBalance) {
        try {
            String json = objectMapper.writeValueAsString(checkingAccountBalance);
            redisTemplate.convertAndSend(checkingBalanceTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
