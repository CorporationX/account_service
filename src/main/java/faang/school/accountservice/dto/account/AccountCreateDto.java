package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountType;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.enums.Currency;

public record AccountCreateDto(
        OwnerType type,
        AccountType accountType,
        Currency currency
) {
}
