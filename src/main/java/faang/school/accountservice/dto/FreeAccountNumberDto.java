package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FreeAccountNumberDto(
        @NotNull
        AccountType type,
        @NotNull
        String number) {
}
