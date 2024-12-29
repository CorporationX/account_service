package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

public record AccountDto(Long id,
                         String accountNumber,
                         AccountOwnerType ownerType,
                         Long ownerId,
                         String ownerName,
                         AccountType accountType,
                         Currency currency,
                         AccountStatus status) {}
