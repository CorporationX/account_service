package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountDto(
        @NotNull
        UUID id,
        @Pattern(regexp = "\\d{12,20}", message = "Account must be between 12 and 20 digits")
        String number,
        Long userId,
        Long projectId,
        @NotNull
        AccountType accountType,
        @NotNull
        Currency currency,
        @NotNull
        AccountStatus status,
        @NotNull
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {
}
