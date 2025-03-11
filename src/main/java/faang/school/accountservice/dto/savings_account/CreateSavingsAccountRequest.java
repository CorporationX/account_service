package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.model.savings_account.SavingsAccount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO for {@link SavingsAccount}
 */
public record CreateSavingsAccountRequest(@Positive @NotNull Long accountId,
                                          @PositiveOrZero BigDecimal balance,
                                          @NotNull @Positive Long startTariffId) {
}