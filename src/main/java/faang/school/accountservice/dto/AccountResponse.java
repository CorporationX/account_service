package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        Long userId,
        Long projectId,
        AccountType type,
        Currency currency,
        BigDecimal balance,
        AccountStatus status

) {
}
