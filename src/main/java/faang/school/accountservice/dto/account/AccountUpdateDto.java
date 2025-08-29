package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountStatus;

public record AccountUpdateDto(
        AccountStatus status
) {
}
