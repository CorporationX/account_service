package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.message.ClearingPaymentEventHandler;
import faang.school.accountservice.message.KafkaRecordConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaClearingPaymentListener {
    private final KafkaRecordConverter kafkaRecordConverter;
    private final ClearingPaymentEventHandler clearingPaymentEventHandler;

    @KafkaListener(topics = "clearing-payment-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, String> record) throws JsonProcessingException {
        if (record.value() == null) {
            log.error("Message from Kafka is null");
            return;
        }

        AuthorizationEvent event = kafkaRecordConverter.convertRecordToObject(record, AuthorizationEvent.class);

        try {
            clearingPaymentEventHandler.handle(event);
        } catch (Exception e) {
            log.error("Error handling AuthorizationEvent: {}", e.getMessage(), e);
        }
    }
}
