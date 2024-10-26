package faang.school.accountservice.dto.filter;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

public record AccountFilterDto(
        AccountType accountType,
        AccountStatus status,
        Currency currency
)
{
}
