package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;

public interface SavingsAccountService {

    SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto);

    SavingsAccountDto getSavingsAccountById(Long savingsAccountId);

    SavingsAccountDto getSavingsAccountByUserId(Long userId);
}
