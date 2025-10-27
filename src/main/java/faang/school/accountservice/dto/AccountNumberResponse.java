package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;

public record AccountNumberResponse(
        Long accountNumber,
        AccountType type
) {
}
