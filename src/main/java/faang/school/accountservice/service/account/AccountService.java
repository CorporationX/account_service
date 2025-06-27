package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;

public interface AccountService {
    AccountDto create(AccountDto accountDto);

    AccountDto update(Long id, AccountDto accountDto);

    AccountDto getAccount(Long id);

    void delete(Long id);
}
