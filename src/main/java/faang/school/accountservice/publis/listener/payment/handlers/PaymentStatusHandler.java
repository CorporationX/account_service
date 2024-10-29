package faang.school.accountservice.publis.listener.payment.handlers;

import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.model.payment.PaymentStatus;

public interface PaymentStatusHandler {
    PaymentStatus requiredStatus();
    void handlePayment(PaymentDto clearingOperationDto);
}
