package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

import java.math.BigDecimal;

public record UpdateAccountDto(
        OwnerType ownerType,
        AccountType type,
        Currency currency,
        AccountStatus status,
        BigDecimal balance,
        String description
) {
}
