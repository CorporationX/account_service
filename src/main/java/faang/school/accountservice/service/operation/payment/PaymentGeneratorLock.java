package faang.school.accountservice.service.operation.payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentGeneratorLock {
    // TODO: логика формирования лока
    public String buildPaymentLock(long userId) {
        return String.valueOf(userId);
    }
}
