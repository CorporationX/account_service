package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.event.PaymentClearEvent;
import faang.school.accountservice.dto.event.PaymentRequestEvent;

public interface OperationService {

    void handlePaymentRequest(PaymentRequestEvent paymentRequestEvent);

    void clearPayment(PaymentClearEvent paymentClearEvent);
}
