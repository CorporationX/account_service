package faang.school.accountservice.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BalanceDto(
        Long accountId,
        BigDecimal actualBalance,
        LocalDateTime createdAt
) {
}
