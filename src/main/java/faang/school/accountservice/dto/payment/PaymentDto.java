package faang.school.accountservice.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(
        UUID accountId,
        BigDecimal amount,
        TypeOperation typeOperation
) {
}
