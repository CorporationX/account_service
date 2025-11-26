package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseAccountDto(
        UUID accountId,
        String accountNumber,
        Long userId,
        Long projectId,
        AccountType type,
        Currency currency,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {
}
