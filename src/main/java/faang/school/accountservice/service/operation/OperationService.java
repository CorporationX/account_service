package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.event.PaymentRequestEvent;

public interface OperationService {

    void handlePaymentRequest(PaymentRequestEvent paymentRequestEvent);
}
