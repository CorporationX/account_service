package faang.school.accountservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDto {
    @Positive
    private long id;
    @Positive
    private long accountId;
    private BigDecimal curAuthBalance;
    private BigDecimal curFactBalance;
}
