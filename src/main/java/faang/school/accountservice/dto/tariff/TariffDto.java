package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.dto.rate.RateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto {
    private Long id;
    private String tariffType;
    private RateDto rateDto;
    private String rateHistory;
}
