package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;

public interface AccountService {
    AccountDto createAccount(CreateAccountDto createAccountDto);

    AccountDto getByAccountId(Long accountId);

    void blockAccount(Long accountId);

    void closeAccount(Long accountId);
}
