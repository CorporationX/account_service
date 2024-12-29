package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.message.AuthorizationEventHandler;
import faang.school.accountservice.message.KafkaRecordConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerListener {
    private final AuthorizationEventHandler authorizationEventHandler;
    private final KafkaRecordConverter kafkaRecordConverter;

    // todo: для каждого топика нужен свой listener
    @KafkaListener(topics = "authorization-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        // Проверка на null
        if (record.value() == null) {
            log.error("Received null message from Kafka");
            return;
        }

        // Извлечение значения из ConsumerRecord
        AuthorizationEvent event = kafkaRecordConverter.convertRecordToObject(record, AuthorizationEvent.class);

        // Обработка полученного события
        try {
            authorizationEventHandler.handle(event);
        } catch (Exception e) {
            log.error("Error handling AuthorizationEvent: {}", e.getMessage(), e);
        }
    }
}
