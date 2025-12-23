package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateBalanceDto(
    @PositiveOrZero BigDecimal authBalance,
    @PositiveOrZero BigDecimal actualBalance
) {
}