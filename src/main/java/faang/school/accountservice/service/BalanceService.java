package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import java.math.BigDecimal;

public interface BalanceService {
    BalanceDto createBalance(Long accountId);
    BalanceDto getBalance(Long accountId);
    BalanceDto updateBalance(Long accountId, BigDecimal newCurrentBalance, BigDecimal newAuthorizedBalance);
}
