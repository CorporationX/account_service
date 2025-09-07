package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.OwnerType;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAccountByOwner(OwnerType ownerType, Long ownerId);

    AccountDto getAccountById(Long accountId);

    AccountDto openAccount(CreateAccountDto accountDto);

    void blockAccount(Long accountId);

    void closeAccount(Long accountId);

}
