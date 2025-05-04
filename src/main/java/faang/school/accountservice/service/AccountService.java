package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;

public interface AccountService {

    AccountDto getAccount(Long id);

    AccountDto openAccount(AccountDto accountDto);

    AccountDto blockAccount(Long id);

    AccountDto closeAccount(Long id);

    Account getAccountEntity(Long id);
}
