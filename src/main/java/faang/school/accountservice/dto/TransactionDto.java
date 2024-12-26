package faang.school.accountservice.dto;

import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        LocalDateTime transactionDate,
        BigDecimal transactionAmount,
        TransactionType transactionType,
        TransactionStatus transactionStatus
) {
}
