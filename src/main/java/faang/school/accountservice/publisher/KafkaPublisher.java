package faang.school.accountservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.Request.RequestEventPub;
import faang.school.accountservice.exception.JsonSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.kafka.topics.request-status}")
    private String topic;

    public void publish(RequestEventPub requestEventPub) {
        try {
            String message = objectMapper.writeValueAsString(requestEventPub);
            kafkaTemplate.send(topic, message);
            log.debug("Published event {} on kafka topic {}", requestEventPub.toString(), topic);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Serialization object %s in json error", requestEventPub.toString());
        }
    }
}
