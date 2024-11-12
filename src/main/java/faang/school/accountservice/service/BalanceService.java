package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;

public interface BalanceService {

    void create(BalanceDto balanceDto);

    void update(BalanceDto balanceDto);

    BalanceDto getBalance(long accountId);

    BalanceDto getBalanceDtoByAccountId(long accountId);

    Balance getBalanceByAccountId(long accountId);
}
