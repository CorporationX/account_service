package faang.school.accountservice.dto.savingsAccount;

import java.math.BigDecimal;

public record BalanceDto(
    Long accountId,
    BigDecimal amount
) {
}
