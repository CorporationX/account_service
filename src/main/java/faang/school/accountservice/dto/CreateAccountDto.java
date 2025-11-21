package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerPerson;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDto(
        @NotNull
        Double balance,
        @NotNull
        Long ownerId,
        @NotNull
        OwnerPerson ownerPerson,
        @NotNull
        AccountType accountType,
        Currency currency
) {
}
