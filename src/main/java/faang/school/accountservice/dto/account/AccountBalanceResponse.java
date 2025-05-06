package faang.school.accountservice.dto.account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountBalanceResponse(
        BigDecimal balance,
        Instant updatedAt,
        Long version
) {
}
