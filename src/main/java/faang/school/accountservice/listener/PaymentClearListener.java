package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClearListener implements KafkaEventListener<String> {

    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.payment-clear.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void onEvent(String jsonEvent, Acknowledgment acknowledgment) {
        try {
            log.info("Received event: {}", jsonEvent);
            List<PendingDto> pendingDto = objectMapper.readValue(jsonEvent, new TypeReference<>() {});
            balanceService.clearPayment(pendingDto);
           acknowledgment.acknowledge();
        } catch (JsonProcessingException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
