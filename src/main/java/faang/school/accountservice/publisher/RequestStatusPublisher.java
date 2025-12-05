package faang.school.accountservice.publisher;

import faang.school.accountservice.dto.request.RequestEventDto;
import faang.school.accountservice.exception.EventPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestStatusPublisher {

    private final KafkaTemplate<String, RequestEventDto> kafkaTemplate;

    @Value("${app.kafka.topics.request-status-events}")
    private String topic;

    @Value("${app.kafka.publisher.timeout-seconds:10}")
    private int timeoutSeconds;

    public void publish(RequestEventDto event) {
        try {
            String key = event.requestId().toString();
            SendResult<String, RequestEventDto> result = kafkaTemplate.send(topic, key, event)
                    .get(timeoutSeconds, TimeUnit.SECONDS);
            log.info("Published request status event with key={} to partition={} offset={}", key,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Failed to publish request status event: {}", event, e);
            throw new EventPublishException("Failed to publish event to Kafka", e);
        }
    }
}