package faang.school.accountservice.event.payment;

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
public class PaymentAuthorizationEventDto {
    @NotNull(message = "operationToken is required")
    private UUID operationToken;

    @NotNull(message = "accountFromId is required")
    private UUID accountFromId;

    @NotNull(message = "accountToId is required")
    private UUID accountToId;

    @NotNull(message = "currencyId is required")
    private UUID currencyId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1", message = "Amount must be at least 1")
    @DecimalMax(value = "10000000", message = "Amount must be at most 10 000 000")
    @Digits(integer = 8, fraction = 0, message = "Amount must be a whole number")
    private BigDecimal amount;

    @NotNull(message = "userId is required")
    private Long userId;
}
