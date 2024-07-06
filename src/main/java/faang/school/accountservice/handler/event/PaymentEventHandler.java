package faang.school.accountservice.handler.event;

import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.enums.payment.PaymentStatus;
import faang.school.accountservice.service.payment.PaymentProcessingServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentEventHandler implements EventHandler<PaymentEvent> {
    private PaymentProcessingServiceImpl paymentProcessingService;

    @Override
    public boolean canHandle(PaymentEvent event) {
        return true;
    }

    @Override
    public void handle(PaymentEvent event) {
        if (event.getType().equals(PaymentStatus.AUTHORIZATION)) {
            paymentProcessingService.reserveMoney(event);
        } if (event.getType().equals(PaymentStatus.CANCELED)) {
            paymentProcessingService.cancelPayment(event);
        }
        if (event.getType().equals(PaymentStatus.CLEARED)) {
            paymentProcessingService.clearing(event);
        }
    }
}