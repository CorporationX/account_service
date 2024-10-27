package faang.school.accountservice.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.service.pending.PendingOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("kafka")
@Component
@RequiredArgsConstructor
public class PendingOperationKafkaListener {
    @Value("${spring.kafka.topic.pending_operation}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    private final ObjectMapper objectMapper;
    private final PendingOperationService pendingOperationService;

    @KafkaListener(topics ="${spring.kafka.topic.pending_operation}", groupId = "${spring.kafka.consumer.group-id}")
    public void pendingOperationListener(String message) {
        try {
            OperationMessage event = objectMapper.readValue(message, OperationMessage.class);
            switch (event.getOperationType()) {
                case AUTHORIZATION -> pendingOperationService.authorization(event);
                case CLEARING -> pendingOperationService.clearing(event);
                case CANCELLATION, ERROR -> pendingOperationService.cancellation(event);
                default -> throw new RuntimeException("error");
            }
        } catch (JsonProcessingException exception) {
            log.error("Unexpected error, listen topic: {} by group: {}", topic, group, exception);
            throw new RuntimeException(exception);
        }
    }
}
