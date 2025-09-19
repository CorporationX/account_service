package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateAccountDto(
        @NotBlank @Pattern(regexp = "\\d{12,20}", message = "accountNumber must be 12–20 digits")
        String accountNumber,
        Long userId,
        Long projectId,
        @NotNull AccountType accountType,
        @NotNull Currency currency,
        AccountStatus accountStatus
) {}
