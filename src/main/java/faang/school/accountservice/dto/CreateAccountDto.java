package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDto(
        Long userId,
        Long projectId,
        @NotNull
        AccountType accountType,
        @NotNull
        Currency currency
) {
}
