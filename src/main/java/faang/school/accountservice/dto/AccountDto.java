package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccountDto(
    Long id,
    String number,
    Long ownerId,
    OwnerType ownerType,
    AccountType type,
    Currency currency,
    AccountStatusType status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime closedAt
) {
}