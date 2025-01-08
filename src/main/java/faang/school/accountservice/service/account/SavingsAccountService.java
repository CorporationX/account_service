package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsAccountService {
    SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto);

    SavingsAccountDto getSavingsAccount(Long id);

    List<SavingsAccountDto> getSavingsAccountByUserId(Long userId);

    void calculatePercent(Long balanceId, BigDecimal rate, Long savingsAccountId);
}