package faang.school.accountservice.dto;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;

public record AccountDto(
        long id,
        String number,
        Account.OwnerType ownerType,
        long ownerId,
        Account.Type type,
        Currency currency,
        String version
) {
}
