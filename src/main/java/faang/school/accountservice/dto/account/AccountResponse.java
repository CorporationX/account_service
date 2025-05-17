package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        Long ownerId,
        OwnerType ownerType,
        AccountType accountType,
        Currency currency,
        BigDecimal balance,
        AccountStatus status

) {
}
