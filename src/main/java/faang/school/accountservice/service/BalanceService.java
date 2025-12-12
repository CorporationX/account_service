package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;

public interface BalanceService {
    BalanceDto create(CreateBalanceDto createBalanceDto);

    BalanceDto update(long balanceId, UpdateBalanceDto updateBalanceDto);

    BalanceDto getById(long balanceId);

    void delete(long balanceId);
}
