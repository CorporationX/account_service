package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.AccountDto;

import java.util.List;

public interface AccountService {

    List<AccountDto> getAllAccountsByOwnerId(Long id);

    AccountDto getAccountById(Long id);

    AccountDto openAccount(long ownerId, AccountDto dto);

    void blockAccount(long ownerId, Long id);

    void closeAccount(long ownerId, Long id);
}
