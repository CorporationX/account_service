package faang.school.accountservice.service.account.number;

import faang.school.accountservice.enums.AccountType;

public interface AccountNumberGenerator {
    long generate(AccountType type, long sequence);
}
