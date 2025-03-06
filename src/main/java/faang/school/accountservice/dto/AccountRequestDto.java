package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

public record AccountRequestDto(
        // Тип владельца счета
        OwnerType ownerType,
        // Тип счета
        AccountType accountType,
        // Код валюты
        Currency currency
) {
}
