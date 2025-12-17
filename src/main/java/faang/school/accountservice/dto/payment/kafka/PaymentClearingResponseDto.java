package faang.school.accountservice.dto.payment.kafka;

import java.util.UUID;

public record PaymentClearingResponseDto(
        UUID transferId,
        PaymentStatus paymentStatus,
        String description

) {
}
