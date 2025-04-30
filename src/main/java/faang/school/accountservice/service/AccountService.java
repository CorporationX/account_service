package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {

    AccountDto getAccount(Long id);

    AccountDto openAccount(AccountDto accountDto);

    AccountDto blockAccount(Long id);

    AccountDto closeAccount(Long id);
}
