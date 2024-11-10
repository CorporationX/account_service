package faang.school.accountservice.publisher.payment.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.aspect.publisher.payment.PaymentPublisher;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractToRedisPaymentPublisher<T> implements PaymentPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Class<T> type;
    @Setter
    private T response;

    @Override
    public void publish() {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.convertAndSend(getTopicName(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> getInstance() {
        return type;
    }
}
