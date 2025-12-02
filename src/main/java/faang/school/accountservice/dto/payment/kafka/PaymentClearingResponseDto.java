package faang.school.accountservice.dto.payment.kafka;

import java.util.UUID;

public record PaymentClearingResponseDto(
        UUID operationId,
        PaymentStatus paymentStatus,
        String description

) {
}
