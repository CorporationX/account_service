package faang.school.accountservice.balance.service;

import faang.school.accountservice.dto.balance.BalanceDto;

public interface BalanceService {

    BalanceDto getBalance(Long balanceId, Long userId);

    BalanceDto createBalance(Long accountId);

    BalanceDto updateBalance(Long balanceId, BalanceDto balanceDto);
}
