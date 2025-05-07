package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;

public interface BalanceService {
    BalanceResponseDto updateBalance(BalanceRequestDto request);

    BalanceResponseDto createBalance(BalanceRequestDto request);

    BalanceResponseDto getBalanceByAccountNumber(String accountNumber);
}
