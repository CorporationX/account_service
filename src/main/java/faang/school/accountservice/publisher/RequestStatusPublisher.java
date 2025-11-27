package faang.school.accountservice.publisher;

import faang.school.accountservice.dto.request.RequestEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestStatusPublisher {

    private final KafkaTemplate<String, RequestEventDto> kafkaTemplate;

    @Value("${app.kafka.topics.request-status-events}")
    private String topic;

    public void publish(RequestEventDto event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Published request status event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish request status event: {}", event, e);
        }
    }
}