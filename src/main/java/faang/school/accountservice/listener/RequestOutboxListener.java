package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.event.RequestOutboxEvent;
import faang.school.accountservice.exception.JsonDeserializationException;
import faang.school.accountservice.handler.RequestOutboxEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestOutboxListener {

    private final ObjectMapper objectMapper;
    private final RequestOutboxEventHandler eventHandler;

    @KafkaListener(
            topics = "${spring.data.kafka.topics.payment-operation.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message) {
        try {
            log.debug("Received new request event: {}", message);
            RequestOutboxEvent event = objectMapper.readValue(message, RequestOutboxEvent.class);
            eventHandler.handle(event);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to event object error", message);
        }
    }
}
