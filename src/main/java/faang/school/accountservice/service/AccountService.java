package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto getAccountById(long id);

    List<AccountDto> getOwnerAccounts(long ownerId, String ownerType);

    AccountDto getAccountByNumber(String number);

    void createAccount(AccountDto dto);

    void blockAccount(long id);

    void closeAccount(long id);
}