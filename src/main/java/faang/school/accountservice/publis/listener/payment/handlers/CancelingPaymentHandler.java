package faang.school.accountservice.publis.listener.payment.handlers;

import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.model.payment.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class CancelingPaymentHandler implements PaymentStatusHandler {
    @Override
    public PaymentStatus requiredStatus() {
        return PaymentStatus.CANCELED;
    }

    @Transactional
    public void handlePayment(PaymentDto paymentDto) {
        log.info("Operation was canceled");
    }
}
