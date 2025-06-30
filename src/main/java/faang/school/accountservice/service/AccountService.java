package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {

    AccountDto openAccount(AccountDto accountDto);

    AccountDto getAccount(String accountNumber);

    void closeAccount(String accountNumber);

    void blockAccount(String accountNumber);
}
