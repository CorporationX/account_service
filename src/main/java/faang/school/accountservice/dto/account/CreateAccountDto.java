package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.account_owner.AccountOwnerDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateAccountDto(
        @NotNull @Size(min = 12, max = 20) String accountNumber,
        @NotNull AccountOwnerDto owner,
        @NotNull Currency currency,
        @NotNull AccountType accountType
) {}
