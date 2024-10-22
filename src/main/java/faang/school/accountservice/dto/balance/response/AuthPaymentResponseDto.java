package faang.school.accountservice.dto.balance.response;

import faang.school.accountservice.model.balance.AuthPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthPaymentResponseDto(
        UUID id,
        UUID balanceId,
        BigDecimal amount,
        AuthPaymentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
