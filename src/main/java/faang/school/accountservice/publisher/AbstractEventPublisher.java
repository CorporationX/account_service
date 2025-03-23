package faang.school.accountservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.Event;
import faang.school.accountservice.exception.PublishEventException;
import faang.school.accountservice.model.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher implements EventPublisher {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void publish(Request request) {
        try {
            String json = objectMapper.writeValueAsString(getEvent(request));
            redisTemplate.convertAndSend(getTopic(), json);
        } catch (JsonProcessingException e) {
            String errorMessage = "Ошибка при публикации ивента";
            log.error("{} : {}", errorMessage, e.getMessage());
            throw new PublishEventException(errorMessage, e);
        }
    }

    protected abstract String getTopic();

    protected abstract Event getEvent(Request request);
}
