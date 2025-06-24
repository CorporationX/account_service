package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);
    AccountDto updateAccount(long accountId, UpdateAccountDto accountDto);
}
