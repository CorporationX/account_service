package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateAccountDto(
        @NotNull @Positive Long accountId,
        AccountStatus status,
        AccountType accountType,
        Currency currency
) {

}
