package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;

public interface BalanceService {
    BalanceDto create(long requesterId, CreateBalanceDto createBalanceDto);

    BalanceDto update(long requesterId, long balanceId, UpdateBalanceDto updateBalanceDto);

    BalanceDto getById(long requesterId, long balanceId);

    void delete(long requesterId, long balanceId);
}
