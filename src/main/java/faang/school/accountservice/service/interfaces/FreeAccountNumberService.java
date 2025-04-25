package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.enums.AccountType;

public interface FreeAccountNumberService {
    String generateAccountNumber(AccountType accountType);
}
