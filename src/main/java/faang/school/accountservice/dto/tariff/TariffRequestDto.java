package faang.school.accountservice.dto.tariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffRequestDto {
    private String tariffType;
    private Double interestRate;
}
