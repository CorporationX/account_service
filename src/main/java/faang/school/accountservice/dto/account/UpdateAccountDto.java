package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountDto(
        @NotNull
        AccountStatus status
) {
}
