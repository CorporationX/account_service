package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.SavingsAccountDto;

public interface SavingsAccountService {
    SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto);
    SavingsAccountDto getSavingsAccount(Long id);
}
