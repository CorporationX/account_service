package faang.school.accountservice.dto.payment.kafka;

import java.util.UUID;

public record PaymentCancelResponseDto(
        UUID transferId,
        PaymentStatus paymentStatus,
        String description

) {
}
