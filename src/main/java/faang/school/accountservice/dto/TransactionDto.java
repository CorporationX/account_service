package faang.school.accountservice.dto;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionDto(
        BigDecimal amount,
        long  balanceId,
        TransactionType type
) {

}
