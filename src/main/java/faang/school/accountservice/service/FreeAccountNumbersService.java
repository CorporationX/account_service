package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;

public interface FreeAccountNumbersService {
    void generateAccountNumbers(AccountType type, int batchSize);
}