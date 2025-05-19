package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceOperationRequest {

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    @PositiveOrZero
    private BigDecimal authorizedAmount;
}
