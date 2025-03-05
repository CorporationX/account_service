package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {
    void createAccount(AccountDto dto);

    AccountDto findAccountById(Long id);

    void deleteAccountById(Long id);

    AccountDto updateAccount(AccountDto dto);
}
