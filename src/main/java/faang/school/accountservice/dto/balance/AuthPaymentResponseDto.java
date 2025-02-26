package faang.school.accountservice.dto.balance;

import faang.school.accountservice.entity.AuthPaymentStatus;
import faang.school.accountservice.entity.Balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthPaymentResponseDto (
    UUID id,
    Balance balance,
    BigDecimal amount,
    AuthPaymentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){
}
