package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;

public interface BalanceService {
    BalanceDto getBalanceById(Long id);
    BalanceDto createBalance(BalanceDto balanceDto);
    BalanceDto updateBalance(BalanceDto balanceDto);
}
