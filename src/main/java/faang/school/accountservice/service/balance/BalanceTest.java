package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.PaymentAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceTest {
    private Long id;
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;
    private PaymentAccount account;
}
