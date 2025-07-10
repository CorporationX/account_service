package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;


public record BalanceDto(
        @NotNull(message = "Balance id cannot be null")

        Long id,
        @NotNull(message = "Authorization balance cannot be null")
        @PositiveOrZero(message = "Authorization balance cannot be negative")
        BigDecimal authorizationBalance,
        @PositiveOrZero(message = "Actual balance cannot be negative")
        @NotNull(message = "Actual balance cannot be null")
        BigDecimal actualBalance) {
}