package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.Owner;
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

        Owner owner,

        @NotNull
        AccountType accountType,

        Currency currency,

        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        Long accountVersion
        ) {
}