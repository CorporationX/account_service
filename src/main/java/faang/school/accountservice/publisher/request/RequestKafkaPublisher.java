package faang.school.accountservice.publisher.request;

import faang.school.accountservice.config.kafka.KafkaRequestTopicProperties;
import faang.school.accountservice.entity.request.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaRequestTopicProperties topicProperties;

    public void sendMessage(Request request) {
        String topicName = topicProperties.getName();
        kafkaTemplate.send(topicName, request)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send request event to Kafka topic '{}'. Error: {}", topicName, ex.getMessage());
                } else {
                    log.info("Request event {} sent to Kafka topic {}", request, topicName);
                }
            });
    }
}
