package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDto(

        Long userId,
        Long projectId,

        @NotNull
        AccountType type,

        @NotNull
        Currency currency
) {
}