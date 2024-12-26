package faang.school.accountservice.dto;

import faang.school.accountservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceChangeDto(
        Long transactionId,
        TransactionType transactionType,
        BigDecimal amount,
        LocalDateTime updatedAt,
        BigDecimal currentBalance
) {
}
