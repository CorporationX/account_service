package faang.school.accountservice.kafka;

import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListeners {

    private final AccountService accountService;

    @KafkaListener(topics = "${app.kafka.topics.authorization}", groupId = "account-service-group")
    public void handleAuthorization(PaymentMessageDto message) {
        log.info("Received AUTHORIZATION message: {}", message);
        accountService.processAuthorization(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.cancel}", groupId = "account-service-group")
    public void handleCancel(PaymentMessageDto message) {
        log.info("Received CANCEL message: {}", message);
        accountService.processCancel(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.clearing}", groupId = "account-service-group")
    public void handleClearing(PaymentMessageDto message) {
        log.info("Received CLEARING message: {}", message);
        accountService.processClearing(message);
    }
}