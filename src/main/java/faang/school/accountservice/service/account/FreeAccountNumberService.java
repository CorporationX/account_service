package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.AccountType;

public interface FreeAccountNumberService {

    String getFreeAccountNumber(AccountType accountType);

}
