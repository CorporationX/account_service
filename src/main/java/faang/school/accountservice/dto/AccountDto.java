package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountDto(
        UUID id,
        String accountNumber,
        Long userId,
        AccountType type,
        Currency currency,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        BigDecimal balance,
        String description,
        String statusChangeReason
) {
}
