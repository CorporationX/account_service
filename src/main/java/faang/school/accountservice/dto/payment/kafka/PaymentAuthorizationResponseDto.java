package faang.school.accountservice.dto.payment.kafka;

import java.util.UUID;

public record PaymentAuthorizationResponseDto(
        UUID transferId,
        PaymentStatus paymentStatus,
        String description

) {
}
