package faang.school.accountservice.service;

import faang.school.accountservice.entity.SavingsAccount;

public interface SavingsAccountService {
    SavingsAccount createSavingsAccount(Long accountId, Long tariffId);
    SavingsAccount getSavingsAccountById(Long id);
    SavingsAccount getSavingsAccountByAccountId(Long accountId);
}
