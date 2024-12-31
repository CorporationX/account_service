package faang.school.accountservice.listener.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public abstract class AbstractEventListener<T> implements EventMessageListener {
    private final ObjectMapper objectMapper;
    private final Class<T> eventType;

    public abstract void handleMessage(T event);

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            T event = objectMapper.readValue(message.getBody(), eventType);
            handleMessage(event);
        } catch (IOException e) {
            log.error("Error deserializing JSON to object", e);
            throw new RuntimeException("Error deserializing JSON to object", e);
        }
    }
}
