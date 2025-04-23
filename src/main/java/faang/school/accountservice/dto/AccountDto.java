package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccountDto(
        long id,
        @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters")
        String number,
        OwnerType ownerType,
        @NotNull(message = "Owner Id cannot be null")
        long ownerId,
        @NotNull(message = "Account type cannot be null")
        AccountType type,
        @NotNull(message = "Currency cannot be null")
        Currency currency,
        @NotNull(message = "Account status cannot be null")
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        long version
) {
}
