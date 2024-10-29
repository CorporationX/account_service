package faang.school.accountservice.publis.listener.payment.handlers;

import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.model.payment.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PendingPaymentHandler implements PaymentStatusHandler {
    @Override
    public PaymentStatus requiredStatus() {
        return PaymentStatus.PENDING;
    }

    @Override
    public void handlePayment(PaymentDto clearingOperationDto) {
        log.info("Ongoing operation");
    }
}
