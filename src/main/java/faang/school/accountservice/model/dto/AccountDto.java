package faang.school.accountservice.model.dto;

import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.model.enums.Currency;
import faang.school.accountservice.model.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccountDto(
        Long id,
        String accountNumber,
        Long ownerId,
        @NotNull(message = "OwnerType must be set")
        OwnerType ownerType,
        @NotNull(message = "AccountType must be set")
        AccountType accountType,
        @NotNull(message = "Currency must be set")
        Currency currency,
        AccountStatus accountStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
