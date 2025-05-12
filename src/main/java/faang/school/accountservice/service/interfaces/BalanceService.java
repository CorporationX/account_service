package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.dto.BalanceOperationDto;
import faang.school.accountservice.dto.BalanceResponseDto;

public interface BalanceService {

    BalanceResponseDto createBalance(Long accountId);

    BalanceResponseDto updateBalance(BalanceOperationDto balanceOperationDto);

    BalanceResponseDto getBalance(Long accountId);
}
