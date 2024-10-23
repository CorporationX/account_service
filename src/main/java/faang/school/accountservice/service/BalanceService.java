package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.BalanceDto;

public interface BalanceService {

    BalanceDto getBalance(long balanceId);

    BalanceDto createBalance(BalanceDto balanceDto);

    BalanceDto updateBalance(BalanceDto balanceDto);
}
