package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BalanceDto {

    private long id;
    private long accountId;
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
}