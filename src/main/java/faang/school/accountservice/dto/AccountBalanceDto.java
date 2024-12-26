package faang.school.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountBalanceDto(
        String accountNumber,
        BigDecimal balance,
        LocalDateTime lastChangeAt
) {
}
