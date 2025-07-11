package faang.school.accountservice.account.strategy;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountType;

public interface AccountTypeSpecifiedAction {

    Account createAccount(Account account);

    AccountType getAccountType();
}
