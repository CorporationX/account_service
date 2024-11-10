package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

public record AccountDto(
        Long id,
        String accountNumber,
        Long ownerId,
        AccountType accountType,
        Currency currency,
        AccountStatus status
) {}
