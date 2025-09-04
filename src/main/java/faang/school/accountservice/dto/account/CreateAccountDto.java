package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

public record CreateAccountDto(
        Long projectId,
        AccountType accountType,
        Currency currency
) {
}
