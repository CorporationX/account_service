package faang.school.accountservice.listener.kafka.listeners.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.exception.ApiException;
import faang.school.accountservice.service.balance.BalanceService;
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
public class ClearingPaymentKafkaListener {
    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;

    @Value("${spring.kafka.topic.clearing-payment.request}")
    private String topicName;

    @KafkaListener(topics = "${spring.kafka.topic.clearing-payment.request}")
    public void onMessage(String message) {
        try {
            ClearingPaymentRequest clearingPaymentRequest = objectMapper.readValue(message, ClearingPaymentRequest.class);
            balanceService.clearingPayment(clearingPaymentRequest);
        } catch (JsonProcessingException exception) {
            log.error("JsonProcessingException when listen topic: {}", topicName, exception);
            throw new ApiException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
