package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.tariff.TariffType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffRequestDto {
    @NotNull
    private TariffType tariffType;
    @NotNull
    private Long interestRateId;
}