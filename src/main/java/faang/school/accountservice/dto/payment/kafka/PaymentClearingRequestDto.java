package faang.school.accountservice.dto.payment.kafka;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentClearingRequestDto(
        UUID senderAccountId,
        UUID recipientAccountId,
        BigDecimal amount,
        UUID operationId
) {
}
