package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateAccountDto(
        @NotNull(message = "The ownerId field is required")
        @Positive(message = "Field ownerId must be positive!")
        Long ownerId,
        @NotNull(message = "OwnerType is required")
        OwnerType ownerType,
        @NotNull(message = "Account type is required")
        AccountType type,
        @NotNull(message = "Currency is required")
        Currency currency
) {
}
