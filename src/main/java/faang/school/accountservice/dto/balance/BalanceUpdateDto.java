package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;

public record BalanceUpdateDto(
        @NotNull(message = "Specify amount")
        Long authorizationAmount
) {
}