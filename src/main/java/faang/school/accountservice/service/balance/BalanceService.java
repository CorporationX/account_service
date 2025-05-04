package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.Balance;

import java.math.BigDecimal;

public interface BalanceService {

    Balance getBalanceByAccountId(Long accountId);

    Balance createBalance(Long accountId, BigDecimal initialBalance);

    Balance authorize(Long accountId, BigDecimal amount);

    Balance clear(Long accountId, BigDecimal amount);

    Balance cancelAuthorization(Long accountId, BigDecimal amount);

    Balance replenish(Long accountId, BigDecimal amount);
}
