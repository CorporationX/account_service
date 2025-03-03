package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;

public interface FreeAccountNumberService {

    String getFreeAccountNumber(AccountType accountType);

}
