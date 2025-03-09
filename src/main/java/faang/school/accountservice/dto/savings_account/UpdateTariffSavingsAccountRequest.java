package faang.school.accountservice.dto.savings_account;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO for {@link faang.school.accountservice.model.savings_account.SavingsAccount}
 */
public record UpdateTariffSavingsAccountRequest(@NotNull @PositiveOrZero Long id,
                                                Long tariffId) {
}