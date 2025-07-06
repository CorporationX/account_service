package faang.school.accountservice.kafka.producer;

import faang.school.accountservice.event.RequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDataSenderImpl implements DataSender {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(String topic, RequestEvent requestEvent) {
        kafkaTemplate.send(topic, requestEvent)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("Sent request event with topic {}, partition = {}, offset ={}",
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.warn("Recommendation event with getIdempotencyKey {} has not been sent",
                                requestEvent.getIdempotencyKey(), ex);
                    }
                });
    }
}
