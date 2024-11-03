package faang.school.accountservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    @Positive
    private long id;
    @Positive
    private long accountId;
    private BigDecimal curAuthBalance;
    private BigDecimal curFactBalance;
}
