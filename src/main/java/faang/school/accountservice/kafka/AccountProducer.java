package faang.school.accountservice.kafka;

import faang.school.accountservice.model.dto.PaymentMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProducer {

    private final KafkaTemplate<String, PaymentMessageDto> kafkaTemplate;

    /**
     * Топик для FAILED платежей в Payment Service
     */
    @Value("${app.kafka.topics.failed}")
    private String failedTopic;

    public void sendFailed(PaymentMessageDto message) {
        kafkaTemplate.send(failedTopic, message.getIdempotencyToken().toString(), message);
        log.error("Отправлено сообщение FAILED в Payment Service: {}", message);
    }
}
