package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

import java.time.LocalDateTime;

public record AccountDto(
        Long id,
        String accountNumber,
        Long userId,
        Long projectId,
        AccountType accountType,
        Currency currency,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {}
