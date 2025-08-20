package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAccountDto(
        @NotNull
        String accountNumber,
        Long userId,
        Long projectId,
        @NotNull
        AccountType accountType,
        @NotNull
        Currency currency
) {}
