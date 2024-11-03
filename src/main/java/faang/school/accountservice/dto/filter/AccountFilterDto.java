package faang.school.accountservice.dto.filter;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;

public record AccountFilterDto (
        @NotNull
        Long ownerId,
        AccountType accountType,
        AccountStatus status,
        Currency currency
) implements FilterDto {}
