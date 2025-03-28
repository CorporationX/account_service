package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;

import java.math.BigDecimal;

public interface BalanceService {
    BalanceResponseDto getBalance(Long balanceId);
    BalanceResponseDto getOrCreateBalance(Long accountId);
    BalanceResponseDto updateBalance(Long balanceId, BigDecimal authorizedBalance, BigDecimal actualBalance);

    BalanceResponseDto topUpBalance(Long balanceId, BigDecimal amount);

    BalanceResponseDto writeOffFunds(Long balanceId, BigDecimal amount);

    BalanceResponseDto holdFunds(Long balanceId, BigDecimal amount);

    BalanceResponseDto releaseFunds(Long balanceId, BigDecimal amount);

    BalanceResponseDto writeOffHeldFunds(Long balanceId, BigDecimal amount);

    boolean hasSufficientFunds(Long balanceId, BigDecimal amount);

    BalanceResponseDto resetBalance(Long balanceId);

    void saveBalanceAudit(BalanceResponseDto balanceResponseDto);
}
