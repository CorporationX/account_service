package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDto(
        UUID id,
        long accountNumber,
        Long userId,
        Long projectId,
        AccountType accountType,
        Currency currency,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        int version
) {
}
