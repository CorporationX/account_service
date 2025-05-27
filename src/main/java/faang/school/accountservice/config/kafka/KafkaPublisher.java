package faang.school.accountservice.config.kafka;

import faang.school.accountservice.dto.Request.RequestEventPub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPublisher {
    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private final KafkaTemplate<String, RequestEventPub> kafkaTemplate;

    public void publish(RequestEventPub requestEventPub) {
        kafkaTemplate.send(topic, requestEventPub.getRequestId().toString(), requestEventPub);
        log.info("Sent request event to kafka: {} " , requestEventPub);
    }
}
