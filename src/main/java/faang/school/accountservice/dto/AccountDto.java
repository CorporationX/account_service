package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AccountDto(
        Long id,

        @Min(12)
        @Max(20)
        @NotNull
        String accountNumber,

        String owner,

        @NotNull
        AccountType accountType,

        @NotNull
        Currency currency,

        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        Long accountVersion
        ) {
}