package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator validator;

    @Transactional(readOnly = true)
    public ResponseAccountDto getAccountWithId(long accountId, long userId) {
        Account account = validator.validateAccount(accountId);
        validator.checkAccountToUser(account, userId);
        log.info("Getting an account with id {} user with id {}", accountId, userId);
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public ResponseAccountDto getAccountWithNumber(String accountNumber, long userId) {
        Account account = validator.validateAccount(accountNumber);
        validator.checkAccountToUser(account, userId);
        log.info("Getting an account with id {} user with id {}", account.getId(), userId);
        return accountMapper.toDto(account);
    }

    @Transactional
    public ResponseAccountDto blockAccount(long accountId, long userId) {
        Account account = validator.validateAccount(accountId);
        validator.checkAccountToUser(account, userId);
        validator.checkStatusOpenAccount(account);
        account.setStatus(AccountStatus.FREEZE);
        accountRepository.save(account);
        log.info("Blocking an account with id {} user with id {}", accountId, userId);
        return accountMapper.toDto(account);
    }

    @Transactional
    public ResponseAccountDto unblockAccount(long accountId, long userId) {
        Account account = validator.validateAccount(accountId);
        validator.checkAccountToUser(account, userId);
        validator.checkStatusFreezeAccount(account);
        account.setStatus(AccountStatus.OPEN);
        accountRepository.save(account);
        log.info("Unblocking an account with id {} user with id {}", accountId, userId);
        return accountMapper.toDto(account);
    }

    @Transactional
    public ResponseAccountDto closeAccount(long accountId, long userId) {
        Account account = validator.validateAccount(accountId);
        validator.checkAccountToUser(account, userId);
        validator.checkStatusCloseAccount(account);
        account.setStatus(AccountStatus.CLOSE);
        accountRepository.save(account);
        account.setClosedAt(OffsetDateTime.now());
        log.info("Closing an account with id {} user with id {}", accountId, userId);
        return accountMapper.toDto(account);
    }

}
