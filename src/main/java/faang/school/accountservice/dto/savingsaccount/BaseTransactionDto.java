package faang.school.accountservice.dto.savingsaccount;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public abstract class BaseTransactionDto {
    @NotBlank(message = "Savings account id is required")
    private Long savingsAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.0", inclusive = false, message = "Amount must be bigger than 10")
    private BigDecimal amount;
}