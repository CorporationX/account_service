package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.tariff.TariffType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link faang.school.accountservice.model.tariff.Tariff}
 */
public record CreateTariffRequest(@NotNull TariffType type,
                                  @NotNull BigDecimal rate) {
}