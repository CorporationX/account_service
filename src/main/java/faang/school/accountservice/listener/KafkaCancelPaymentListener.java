package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.message.ClearingPaymentEventHandler;
import faang.school.accountservice.message.KafkaRecordConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCancelPaymentListener {
    private final ObjectMapper objectMapper;
    private final KafkaRecordConverter kafkaRecordConverter;
    private final ClearingPaymentEventHandler clearingPaymentEventHandler;

    @KafkaListener(topics = "clearing-payment-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        // Проверка на null
        if (record.value() == null) {
            log.error("Message from Kafka is null");
            return;
        }

        // Извлечение значения из ConsumerRecord
        AuthorizationEvent event = kafkaRecordConverter.convertRecordToObject(record, AuthorizationEvent.class);

        // Обработка полученного события
        try {
            clearingPaymentEventHandler.handle(event);
        } catch (Exception e) {
            log.error("Error handling AuthorizationEvent: {}", e.getMessage(), e);
        }
    }
}
