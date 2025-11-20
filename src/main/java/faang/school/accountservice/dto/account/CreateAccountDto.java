package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountDto(
        @NotNull
        @Size(min = 12, max = 20)
        String accountNumber,

        Long userId,
        Long projectId,

        @NotNull
        AccountType type,

        @NotNull
        Currency currency
) {
}