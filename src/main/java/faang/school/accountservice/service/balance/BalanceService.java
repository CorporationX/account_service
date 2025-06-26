package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Balance;

import java.math.BigDecimal;

public interface BalanceService {
    BalanceDto create(BalanceDto balanceDto);
    BalanceDto update(Long id, BalanceDto balanceDto);
    BalanceDto getBalance(Long id);
}