package faang.school.accountservice.service.operation;

import faang.school.accountservice.listener.event.PaymentRequestEvent;

public interface OperationService {

    void save(PaymentRequestEvent paymentRequestEvent);
}
