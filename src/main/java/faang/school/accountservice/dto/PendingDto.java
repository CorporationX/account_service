package faang.school.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingDto {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount should have up to 10 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "From Account ID is required")
    @Positive(message = "From account ID must be positive")
    private Long fromAccountId;

    @Positive(message = "To account ID must be positive")
    @NotNull(message = "To Account ID is required")
    private Long toAccountId;

    private PendingStatus status;
}

