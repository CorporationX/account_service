package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.event.PaymentEvent;

public interface PaymentProcessingService {
    void reserveMoney(PaymentEvent paymentEvent);
    void cancelPayment(PaymentEvent paymentEvent);
    void clearing(PaymentEvent paymentEvent);
    void passPayment(PaymentEvent paymentEvent);
}