package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;

public interface BalanceService {
    void create(BalanceDto balanceDto);

    void update(BalanceDto balanceDto);

    BalanceDto getBalance(long accountId);
}
