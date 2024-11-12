package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.entity.tariff.TariffType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffResponseDto {
    private Long id;
    private TariffType tariffType;
    private List<Double> rates;
}
