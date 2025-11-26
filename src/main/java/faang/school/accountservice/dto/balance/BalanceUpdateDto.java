package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BalanceUpdateDto(
        @NotNull(message = "Specify amount")
        @Positive(message = "Amount cannot be negative")
        Long authorizationAmount
) {
}