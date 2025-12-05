package faang.school.accountservice.dto.payment.kafka;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCancelRequestDto(
        UUID accountId,
        BigDecimal amount,
        UUID transferId
) {
}
