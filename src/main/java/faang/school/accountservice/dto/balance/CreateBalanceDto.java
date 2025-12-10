package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateBalanceDto(
    @NonNull UUID accountId,
    @NonNull @PositiveOrZero BigDecimal actualAmount
) {
}