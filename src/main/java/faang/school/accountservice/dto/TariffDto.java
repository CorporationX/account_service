package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountRate;
import faang.school.accountservice.enums.TariffType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TariffDto {
    private TariffType type;
    private Double currentRate;
    private List<Double> bettingHistory;
}
