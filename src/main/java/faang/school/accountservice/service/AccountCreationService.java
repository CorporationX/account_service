package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.model.Account;

/**
 * @author Evgenii Malkov
 */
public interface AccountCreationService {

    void create(Account account, TariffType tariffType);

    AccountType getAccountType();
}
