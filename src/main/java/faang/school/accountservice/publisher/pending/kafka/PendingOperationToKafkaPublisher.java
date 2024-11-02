package faang.school.accountservice.publisher.pending.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
@Component
public class PendingOperationToKafkaPublisher implements PendingOperationStatusPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.checking_balance}")
    private String topicName;

    @Override
    public void publish(CheckingAccountBalance checkingAccountBalance) {
        try {
            String json = objectMapper.writeValueAsString(checkingAccountBalance);
            kafkaTemplate.send(topicName, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
