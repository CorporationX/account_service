package faang.school.accountservice.service.impl;

import faang.school.accountservice.model.dto.AccountDto;

public interface AccountService {
    AccountDto getAccount(Long id);
    AccountDto openAccount(AccountDto accountDto);
    AccountDto blockAccount(Long id);
    AccountDto blockAcccountNumber(String number);
}
