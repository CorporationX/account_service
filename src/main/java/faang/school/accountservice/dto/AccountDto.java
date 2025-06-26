package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

public record AccountDto(
        Long id,
        String number,
        OwnerType ownerType,
        long ownerId,
        AccountType type,
        Currency currency
) {
}
