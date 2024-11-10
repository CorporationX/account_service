package faang.school.accountservice.listener.redis.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;

@Slf4j
@AllArgsConstructor
public abstract class AbstractEventListener<T> implements EventMessageListener {
    private final ObjectMapper objectMapper;
    private final Class<T> eventType;

    public abstract void saveEvent(T event);

    public abstract void handleException(Exception exception);

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            T event = objectMapper.readValue(message.getBody(), eventType);
            saveEvent(event);
        } catch (Exception exception) {
            handleException(exception);
        }
    }
}
