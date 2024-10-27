package faang.school.accountservice.util;

import faang.school.accountservice.enums.AccountType;

public interface AccountNumberGenerator {

    String generateAccountNumber(AccountType type, Long number);
}
