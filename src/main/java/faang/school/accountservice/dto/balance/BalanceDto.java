package faang.school.accountservice.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceDto(
        Long id,
        Long accountId,
        BigDecimal authBalance,
        BigDecimal actualBalance
) {
}