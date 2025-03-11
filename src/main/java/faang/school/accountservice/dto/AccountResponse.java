package faang.school.accountservice.dto;

import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.model.account.enums.OwnerType;

public record AccountResponse(
        Long id,
        String number,
        Long ownerId,
        OwnerType ownerType,
        AccountType type
) {
}
