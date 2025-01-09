package faang.school.accountservice.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRecordConverter {
    private final ObjectMapper objectMapper;

    public <T> T convertRecordToObject(ConsumerRecord<String, String> record, Class<T> targetType) throws JsonProcessingException {
        String inputRecord = record.value();
        T event = objectMapper.readValue(inputRecord, targetType);

        log.info("Received AuthorizationEvent: {}", inputRecord);

        return event;
    }
}
