package faang.school.accountservice.dto.tariff;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private String rateHistory;
}
