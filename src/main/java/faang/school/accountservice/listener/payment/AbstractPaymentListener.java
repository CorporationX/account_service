package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.exception.ListenerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;

@Slf4j
public abstract class AbstractPaymentListener<T> {

    private final ObjectMapper objectMapper;

    public AbstractPaymentListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void listen(String event) {
        if (event == null || event.trim().isEmpty()) {
            log.error("Received empty or null event");
            return;
        }

        try {
            T paymentEvent = objectMapper.readValue(event, getEventClass());
            log.info("Received event {}", event);
            processEvent(paymentEvent);
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }

    protected abstract Class<T> getEventClass();

    protected abstract void processEvent(T event);
}