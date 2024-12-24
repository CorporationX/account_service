package faang.school.accountservice.dto.interest_rate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterestRateDto {
    private Long id;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal interestRate;
}