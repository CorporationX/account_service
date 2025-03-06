package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.enums.TariffType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record TariffRequestDto(
    @NotNull TariffType name,
    @NotNull @Positive Double rate
) {
}
