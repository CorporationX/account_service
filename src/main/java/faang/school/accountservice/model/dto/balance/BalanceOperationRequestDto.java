package faang.school.accountservice.model.dto.balance;

import java.math.BigDecimal;

public record BalanceOperationRequestDto(
        BigDecimal amount
) {
}
