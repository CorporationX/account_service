package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record BalanceDto(
        @NotNull(message = "Balance id cannot be null")
        Long id,
        @NotNull(message = "Authorization balance cannot be null")
        BigDecimal authorizationBalance,
        @NotNull(message = "Actual balance cannot be null")
        BigDecimal actualBalance) {
}