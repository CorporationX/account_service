package faang.school.accountservice.service.savings;

import faang.school.accountservice.model.SavingsAccount;

public interface SavingsAccountService {

    SavingsAccount openSavingsAccount(Long accountId, Long initialTariffId);

    SavingsAccount getSavingsAccount(Long id);

    SavingsAccount getSavingsAccountByClientId(Long clientId);
}
