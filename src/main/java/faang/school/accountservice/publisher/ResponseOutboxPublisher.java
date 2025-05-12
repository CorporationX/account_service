package faang.school.accountservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.properties.PaymentResponseTopicProperties;
import faang.school.accountservice.dto.event.ResponseOutboxEvent;
import faang.school.accountservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseOutboxPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentResponseTopicProperties properties;

    public void publish(ResponseOutboxEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(properties.name(), message);
            log.debug("Published payment response event {} on kafka topic {}", event.toString(), properties.name());
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %s in json error", event.toString());
        }
    }
}
