package faang.school.accountservice.publisher.payment;

import faang.school.accountservice.config.kafka.KafkaFailedPaymentAuthorizationResTopicProperties;
import faang.school.accountservice.event.payment.FailedPaymentAuthorizationEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
// TODO: можно логику венести в абстрактный класс
public class FailedPaymentAuthorizationPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaFailedPaymentAuthorizationResTopicProperties failedProps;

    public void sendMessage(FailedPaymentAuthorizationEventDto event) {
        kafkaTemplate.send(failedProps.getName(), event).thenAccept(result ->
                        log.info("FailedPaymentAuthorization event {} sent to Kafka topic {}",
                                event, failedProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send FailedPaymentAuthorization event to Kafka topic '{}'. Error: {}",
                            failedProps.getName(), ex.getMessage());
                    return null;
                });
    }
}
