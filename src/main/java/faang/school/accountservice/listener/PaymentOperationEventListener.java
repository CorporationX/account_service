package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.event.PaymentOperationEvent;
import faang.school.accountservice.exception.JsonDeserializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOperationEventListener {

    private final ObjectMapper objectMapper;

    public void receive(String message) {
        try {
            log.debug("Received new hashtag event: {}", message);
            PaymentOperationEvent event = objectMapper.readValue(message, PaymentOperationEvent.class);
            // вызов метода обработки
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to event object error", message);
        }
    }
}
