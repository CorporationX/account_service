package faang.school.accountservice.dto.savingsAccount;

import faang.school.accountservice.enums.TariffType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SavingsAccountRequestDto(
    @NotNull @Positive Long accountId,
    @NotNull TariffType tariffName
) {
}
