package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;

public interface BalanceService {

    BalanceResponseDto createBalance(long accountId);

    BalanceResponseDto updateBalance(long accountId, BalanceUpdateDto balanceUpdateDto);

    BalanceResponseDto getBalance(long balanceId);
}