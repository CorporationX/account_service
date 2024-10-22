package faang.school.accountservice.service.impl;

import faang.school.accountservice.model.dto.AccountDto;

import java.util.List;

public interface AccountService {
    AccountDto getAccount(Long id);

    AccountDto openAccount(AccountDto accountDto);

    AccountDto blockAccount(Long id);

    AccountDto blockAccountNumber(String number);

    List<AccountDto> blockAllUserAccounts(Long id);

    AccountDto unblockAccount(Long id);

    AccountDto unblockAccountNumber(String number);

    List<AccountDto> unblockAllUserAccounts(Long id);

    AccountDto closeAccount(Long id);

    AccountDto closeAccountNumber(String number);
}
