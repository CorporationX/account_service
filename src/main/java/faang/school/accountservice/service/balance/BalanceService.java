package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;

public interface BalanceService {
    void updateBalance(BalanceRequestDto request);

    void createBalance(BalanceRequestDto request);

    BalanceResponseDto getBalanceByAccountNumber(String accountNumber);
}
