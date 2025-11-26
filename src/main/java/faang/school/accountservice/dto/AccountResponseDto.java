package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;

public record AccountResponseDto(
        Long id,
        AccountStatusType status,
        Currency currency,
        OwnerType ownerType,
        AccountType type
) {
}