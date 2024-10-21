package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.enumeration.TariffType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffCreateDto {
    @NotNull
    private TariffType tariffType;

    @NotNull
    @Positive
    private Double rate;
}
