package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.enums.BalanceChangeType;
import faang.school.accountservice.service.balance.BalanceChange;

public interface BalanceService {

    void registerBalanceChange(BalanceChangeType balanceChangeType, BalanceChange balanceChange);

    BalanceDto getBalanceByAccountId(Long accountId);

    BalanceDto getBalanceById(Long balanceId);

    BalanceDto createBalance(Long accountId);

    BalanceDto changeBalance(Long balanceId, AmountChangeRequest amount);

    BalanceDto reserveBalance(Long balanceId, AmountChangeRequest amount);
}
