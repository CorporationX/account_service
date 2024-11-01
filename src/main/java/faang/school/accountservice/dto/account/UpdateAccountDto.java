package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.Builder;

@Builder
public record UpdateAccountDto(
        AccountStatus status,
        AccountType accountType,
        Currency currency
) {}
