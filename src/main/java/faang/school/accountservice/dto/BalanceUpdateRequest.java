package faang.school.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceUpdateRequest(
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount
) {
}
