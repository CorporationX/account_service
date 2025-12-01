package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.ChangedBalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;

public interface BalanceService {
    BalanceDto create(CreateBalanceDto createBalanceDto);

    BalanceDto update(long balanceId, UpdateBalanceDto updateBalanceDto);

    BalanceDto getBalanceById(long balanceId);

    BalanceDto authorize(long balanceId, ChangedBalanceDto authorizeBalanceDto);

    BalanceDto confirm(long balanceId, ChangedBalanceDto confirmBalanceDto);

    BalanceDto release(long balanceId, ChangedBalanceDto releaseBalanceDto);
}