package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;

public interface FreeAccountNumberService {

    void generateAccountNumbers(AccountType type, int batchSize);

    Long retrieveAccountNumber(AccountType type);
}
