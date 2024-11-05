package faang.school.accountservice.service;

import faang.school.accountservice.model.event.PaymentEvent;

public interface PaymentRequestService {
    void authorize(PaymentEvent paymentEvent);
    void cancel(PaymentEvent paymentEvent);
    void clearing(PaymentEvent paymentEvent);
}
