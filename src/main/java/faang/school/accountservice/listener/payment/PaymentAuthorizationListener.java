package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.kafka.topics.KafkaPaymentAuthorizationReqTopicProperties;
import faang.school.accountservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.accountservice.facade.operation.payment.PaymentKafkaFacade;
import faang.school.accountservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentAuthorizationListener extends AbstractKafkaListener<PaymentAuthorizationEventDto> {
    private final PaymentKafkaFacade paymentKafkaFacade;
    private final KafkaPaymentAuthorizationReqTopicProperties paymentProps;

    public PaymentAuthorizationListener(ObjectMapper objectMapper,
                                        Class<PaymentAuthorizationEventDto> eventClass,
                                        PaymentKafkaFacade paymentKafkaFacade,
                                        KafkaPaymentAuthorizationReqTopicProperties paymentProp) {
        super(objectMapper, eventClass);
        this.paymentKafkaFacade = paymentKafkaFacade;
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
        paymentKafkaFacade.authorizePayment(event);
    }

//    @KafkaListener(topics = "${spring.kafka.topic.payment-authorization-request.name}",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void listenPaymentAuthorizationTopic(
//            @Valid @Payload PaymentAuthorizationEventDto event) {
//
//        log.info("Received: {}", event);
//        Operation op = paymentService.createPaymentOperation(event);
//        successPaymentAuthorizationPublisher.sendMessage(
//                new SuccessPaymentAuthorizationEventDto(op.getId(), LocalDateTime.now()));
//    }
}
