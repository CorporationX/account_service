package faang.school.accountservice.dto.payment;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentStatus;

import java.math.BigDecimal;
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
