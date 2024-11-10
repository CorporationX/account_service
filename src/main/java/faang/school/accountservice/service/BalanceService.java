package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;

public interface BalanceService {

    BalanceDto getBalanceByAccountId(Long accountId);

    BalanceDto getBalanceById(Long balanceId);

    BalanceDto createBalance(Long accountId);

    BalanceDto changeBalance(Long balanceId, AmountChangeRequest request);
}
