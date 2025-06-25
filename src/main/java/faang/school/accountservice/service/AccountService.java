package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;

public interface AccountService {

    AccountDto open(AccountDto accountDto);
    AccountDto get(String accountNumber);
    void close(String accountNumber);
    void block(String accountNumber);
    AccountDto update(long accountId, UpdateAccountDto accountDto);
}
