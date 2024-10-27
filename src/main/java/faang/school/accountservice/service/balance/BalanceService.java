package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.PendingOperation;

public interface BalanceService {

    BalanceDto getBalance(Long balanceId, Long userId);

    BalanceDto createBalance(Long accountId);

    BalanceDto updateBalance(Long balanceId, BalanceDto balanceDto, PendingOperation operation);
}
