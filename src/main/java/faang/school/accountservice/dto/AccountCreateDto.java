package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountCreateDto(
        @NotNull
        AccountType type,
        @NotNull
        Currency currency,
        @Size(max = 255)
        String description
) {
}

