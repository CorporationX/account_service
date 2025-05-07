package faang.school.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceRequestDto {
    @NotNull(message = "Account Number can't be null")
    @NotEmpty(message = "Account Number can't be empty")
    private String accountNumber;

    @NotNull(message = "Authorization Balance can't be null")
    @DecimalMin(value = "0.0", message = "Authorization Balance can't be negative")
    private BigDecimal authorizationBalance;

    @NotNull(message = "Factual Balance can't be null")
    @DecimalMin(value = "0.0", message = "Factual Balance can't be negative")
    private BigDecimal factualBalance;
}
