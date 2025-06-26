package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;

public interface BalanceService {
    BalanceDto create(BalanceDto balanceDto);

    BalanceDto update(Long id, BalanceDto balanceDto);

    BalanceDto getBalance(Long id);

    void delete(Long id);
}