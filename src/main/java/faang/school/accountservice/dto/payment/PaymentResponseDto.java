package faang.school.accountservice.dto.payment;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponseDto(

        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
