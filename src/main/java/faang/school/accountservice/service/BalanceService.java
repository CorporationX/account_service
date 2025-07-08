package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;

public interface BalanceService {
    BalanceDto getBalanceById(Long balanceId);
    void createBalance(Long accountId);
    BalanceDto updateBalance(BalanceDto balanceDto);
}