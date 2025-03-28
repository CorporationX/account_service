package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

public record AccountRequestDto(
        OwnerType ownerType,
        AccountType accountType,
        Currency currency
) {
}
