package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BalanceDto {
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;
}
