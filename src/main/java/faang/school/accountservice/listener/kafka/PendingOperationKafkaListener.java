package faang.school.accountservice.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.exception.ApiException;
import faang.school.accountservice.exception.pending.UnknownOperationException;
import faang.school.accountservice.service.pending.PendingOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
@RequiredArgsConstructor
@Component
public class PendingOperationKafkaListener {
    @Value("${spring.kafka.topic.pending_operation}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    private final ObjectMapper objectMapper;
    private final PendingOperationService pendingOperationService;

    @KafkaListener(topics ="${spring.kafka.topic.pending_operation}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) {
        try {
            OperationMessage operation = objectMapper.readValue(message, OperationMessage.class);
            switch (operation.getStatus()) {
                case AUTHORIZATION -> pendingOperationService.authorization(operation);
                case CLEARING -> pendingOperationService.clearing(operation);
                case CANCELLATION, ERROR -> pendingOperationService.cancellation(operation);
                default -> throw new UnknownOperationException(operation.getStatus());
            }
        } catch (JsonProcessingException exception) {
            log.error("Unexpected error, listen topic: {} of group: {}", topic, group, exception);
            throw new ApiException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
