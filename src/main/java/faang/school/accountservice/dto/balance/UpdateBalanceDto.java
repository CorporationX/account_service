package faang.school.accountservice.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateBalanceDto(
        BigDecimal authorizedBalance,
        BigDecimal actualBalance
) {
}
