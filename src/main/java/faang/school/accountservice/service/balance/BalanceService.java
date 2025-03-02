package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;

public interface BalanceService {
    BalanceDto getBalanceByAccountId(long accountId);

    BalanceDto createBalance(BalanceDto dto);

    BalanceDto updateBalance(BalanceDto dto);
}