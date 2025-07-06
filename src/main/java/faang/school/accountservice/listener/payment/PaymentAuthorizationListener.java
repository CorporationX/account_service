package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.kafka.KafkaPaymentAuthorizationReqTopicProperties;
import faang.school.accountservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.accountservice.listener.AbstractKafkaListener;
import faang.school.accountservice.service.operation.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentAuthorizationListener extends AbstractKafkaListener<PaymentAuthorizationEventDto> {
    private final OperationService operationService;
    private final KafkaPaymentAuthorizationReqTopicProperties paymentProps;

    public PaymentAuthorizationListener(ObjectMapper objectMapper,
                                        Class<PaymentAuthorizationEventDto> eventClass,
                                        OperationService operationService,
                                        KafkaPaymentAuthorizationReqTopicProperties paymentProp) {
        super(objectMapper, eventClass);
        this.operationService = operationService;
        this.paymentProps = paymentProp;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.payment-authorization-request.name}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    // TODO: авто коммит
    public void listenPaymentAuthorizationTopic(String message) {
        PaymentAuthorizationEventDto event = getEvent(message);
        log.info("Received a message from {}: {}", paymentProps.getName(), event);
        operationService.createPaymentOperation(event);
    }
}
