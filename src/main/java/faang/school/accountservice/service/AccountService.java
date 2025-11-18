package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {
    AccountDto getAccount(String accountNumber);

    AccountDto openAccount(AccountDto accountDto);

    AccountDto blockAccount(String accountNumber);

    AccountDto unblockAccount(String accountNumber);

    AccountDto closeAccount(String accountNumber);

}