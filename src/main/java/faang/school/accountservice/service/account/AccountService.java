package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account getAccountById(UUID uuid) {
        return accountRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.error("Account with {} not found", uuid);
                    return new AccountNotFoundException(uuid);
                });
    }

    @Transactional
    public Account createAccount(Account account) {
        Account savedAccount = accountRepository.save(account);
        log.info("Account {} has been saved", savedAccount);

        return savedAccount;
    }
}
