package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1", message = "Amount must be at least 1")
    @DecimalMax(value = "10000000", message = "Amount must be at most 10 000 000")
    @Digits(integer = 20, fraction = 0, message = "Amount must be a whole number")
    BigDecimal amount;
}
