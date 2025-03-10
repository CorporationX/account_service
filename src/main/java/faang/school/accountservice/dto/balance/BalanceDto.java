package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceDto(
        long id,

        @NotNull(message = "Account id can't be null")
        @Min(value = 1, message = "Account id should be more than 0")
        long accountId,

        @NotNull(message = "Authorization balance can't be null")
        @Min(value = 1, message = "Authorization balance should be more than 0")
        BigDecimal authorizationBalance,

        @NotNull(message = "Actual balance can't be null")
        @Min(value = 1, message = "Actual balance should be more than 0")
        BigDecimal actualBalance
) {
}