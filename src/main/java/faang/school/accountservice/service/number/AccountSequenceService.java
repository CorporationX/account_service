package faang.school.accountservice.service.number;

import faang.school.accountservice.enums.AccountType;

public interface AccountSequenceService {
    AccountPeriod incrementCounter(AccountType type, int batchSize);
}