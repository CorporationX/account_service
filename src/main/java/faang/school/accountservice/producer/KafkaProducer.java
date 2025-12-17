package faang.school.accountservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducer {

    public final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, Object objectToSend) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, objectToSend);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(producerRecord);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent event {} to topic '{}', partition: {}, offset: {}",
                        objectToSend,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event {} to topic '{}': {}",
                        objectToSend,
                        topic,
                        ex.getMessage());
            }
        });
    }
}
