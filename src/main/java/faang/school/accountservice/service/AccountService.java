package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;

public interface AccountService {

    AccountDto getAccountById(long id);

    AccountDto openAccount(AccountDto account);

    AccountDto frozeAccount(long id);

    AccountDto blockAccount(long id);

    AccountDto closeAccount(long id);
}
