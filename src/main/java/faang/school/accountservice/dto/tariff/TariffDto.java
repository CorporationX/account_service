package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.tariff.TariffType;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for {@link faang.school.accountservice.model.tariff.Tariff}
 */
public record TariffDto(Long id,
                        TariffType type,
                        List<BigDecimal> rateHistory) {
}