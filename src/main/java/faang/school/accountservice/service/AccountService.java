package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountPreviewDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.OwnerRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    AccountDto openAccount(CreateAccountDto createAccountDto);

    void freezeAccount(Long accountId);

    void unfreezeAccount(Long accountId);

    void closeAccount(Long accountId);

    AccountDto getAccountById(Long id);

    List<AccountPreviewDto> findAccountsByOwner(OwnerRequest ownerRequest, Pageable pageable);
}
