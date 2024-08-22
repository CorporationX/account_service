package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        return null;
    }

    @Transactional
    public AccountDto closeAccount(AccountDto accountDto) {
        return null;
    }

    @Transactional
    public AccountDto blockAccount(AccountDto accountDto) {
        return null;
    }

    @Transactional(readOnly = true)
    public AccountDto getAccount(AccountDto accountDto) {
        return null;
    }
}