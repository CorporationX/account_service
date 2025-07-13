package faang.school.accountservice.publisher.payment;

import faang.school.accountservice.config.kafka.topics.KafkaSuccessPaymentAuthorizationResTopicProperties;
import faang.school.accountservice.event.payment.SuccessPaymentAuthorizationEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessPaymentAuthorizationPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaSuccessPaymentAuthorizationResTopicProperties successProps;

    public void sendMessage(SuccessPaymentAuthorizationEventDto event) {
        kafkaTemplate.send(successProps.getName(), event).thenAccept(result ->
                        log.info("SuccessPaymentAuthorization event {} sent to Kafka topic {}",
                                event, successProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send SuccessPaymentAuthorization event to Kafka topic '{}'. Error: {}",
                            successProps.getName(), ex.getMessage());
                    return null;
                });
    }
}
