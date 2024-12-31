package faang.school.accountservice.publisher.payment.abstracts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractPaymentPublisher<T> implements PaymentPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    @Setter
    private T response;

    @Override
    public Class<T> getInstance() {
        return type;
    }

    @Override
    public void publish() {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.convertAndSend(getTopicName(), json);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize response", ex);
        }
    }
}

