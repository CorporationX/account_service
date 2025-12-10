package faang.school.accountservice.dto.balance;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseBalanceDto(
    Long balanceId,
    UUID accountId,
    BigDecimal actualBalance
) {
}