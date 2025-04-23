package faang.school.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record SavingsAccountResponse(
        String accountNumber,
        BigDecimal balance,
        LocalDateTime lastInterestAccrualAt,
        Map<String, List<BigDecimal>> tariffHistory,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
