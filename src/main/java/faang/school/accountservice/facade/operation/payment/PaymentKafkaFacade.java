package faang.school.accountservice.facade.operation.payment;

import faang.school.accountservice.entity.operation.Operation;
import faang.school.accountservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.accountservice.event.payment.SuccessPaymentAuthorizationEventDto;
import faang.school.accountservice.publisher.payment.SuccessPaymentAuthorizationPublisher;
import faang.school.accountservice.service.operation.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaFacade {
    private PaymentService paymentService;
    private SuccessPaymentAuthorizationPublisher successPaymentAuthorizationPublisher;

    public void authorizePayment(PaymentAuthorizationEventDto paymentEvent) {
        Operation paymentOperation = paymentService.createPaymentOperation(paymentEvent);
        SuccessPaymentAuthorizationEventDto responseEventDto =
                new SuccessPaymentAuthorizationEventDto(paymentOperation.getId(), LocalDateTime.now());
        successPaymentAuthorizationPublisher.sendMessage(responseEventDto);
    }
}
