package faang.school.accountservice.dto.tariff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for {@link faang.school.accountservice.model.tariff.Tariff}
 */
public record AddRateTariffRequest(@NotNull @Positive Long id,
                                   @NotNull @Positive BigDecimal rate) {
}