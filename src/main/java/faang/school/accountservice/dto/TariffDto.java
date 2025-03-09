package faang.school.accountservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TariffDto {
    private String tariffName;
    private String rateHistory;
}
