package faang.school.accountservice.service;

import faang.school.accountservice.model.Balance;
import java.math.BigDecimal;

public interface BalanceService {
    Balance createBalance(Long accountId);
    Balance getBalance(Long accountId);
    Balance updateBalance(Long accountId, BigDecimal newCurrentBalance, BigDecimal newAuthorizedBalance);
}
