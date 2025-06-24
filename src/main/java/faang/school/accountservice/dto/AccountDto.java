package faang.school.accountservice.dto;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;

public record AccountDto(
        Long id,
        Account.Owner owner,
        long ownerId,
        Account.Type type,
        Currency currency,
        String version
) {
}
