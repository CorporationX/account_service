package faang.school.accountservice.dto;

import java.math.BigDecimal;

public record BalanceResponseDto(
    BigDecimal currentBalance,
    BigDecimal availableBalance) {
}
