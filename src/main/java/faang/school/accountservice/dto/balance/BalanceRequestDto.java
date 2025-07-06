package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceRequestDto {
    @NotNull(message = "id is mandatory")
    private UUID id;
    @NotNull(message = "amount is mandatory")
    @Min(value = 0, message = "Amount must be positive")
    private BigDecimal amount;

    @AssertTrue(message = "Amount must be a whole number")
    public boolean isWholeAmount() {
        return amount != null && amount.stripTrailingZeros().scale() <= 0;
    }
}
