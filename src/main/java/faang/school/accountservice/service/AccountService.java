package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {

    AccountDto open(AccountDto accountDto);

    AccountDto get(String accountNumber);

    void close(String accountNumber);

    void block(String accountNumber);
}
