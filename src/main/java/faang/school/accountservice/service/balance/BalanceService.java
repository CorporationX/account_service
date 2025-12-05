package faang.school.accountservice.service.balance;

import java.math.BigDecimal;

public interface BalanceService {
    BigDecimal getBalance(Long accountId);

    BigDecimal getBalanceWithLock(Long accountId);
}

