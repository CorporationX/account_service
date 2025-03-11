package faang.school.accountservice.dto;

import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.model.account.enums.Currency;
import faang.school.accountservice.model.account.enums.OwnerType;

public record AccountRequest(
        Long ownerId,
        OwnerType ownerType,
        AccountType type,
        Currency currency
) {
}
