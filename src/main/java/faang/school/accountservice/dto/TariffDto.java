package faang.school.accountservice.dto;

import faang.school.accountservice.entity.TariffRateHistory;
import faang.school.accountservice.enums.TariffType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TariffDto {
    private TariffType tariffType;
    private List<TariffRateHistory> rateHistory;
}
