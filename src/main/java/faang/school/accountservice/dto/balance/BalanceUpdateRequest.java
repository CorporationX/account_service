package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceUpdateRequest {
    private BigDecimal authorisationBalance;
    private BigDecimal actualBalance;
}