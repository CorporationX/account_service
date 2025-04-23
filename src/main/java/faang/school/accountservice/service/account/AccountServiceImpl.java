package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static faang.school.accountservice.messages.ErrorMessages.ACCOUNT_NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponseDto getAccount(String accountNumber) {
        return accountMapper.toAccountResponseDto(findAccount(accountNumber));
    }

    @Override
    public AccountResponseDto createAccount(AccountRequestDto accountRequest) {
        String accountNumber = generateAccountNumber();
        Account account = accountMapper.toAccount(accountRequest);
        log.info("Creating account with number {} for user owner ID {}", accountNumber, accountRequest.getOwnerId());
        account.setAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.ACTIVE);
        return accountMapper.toAccountResponseDto(accountRepository.save(account));
    }

    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${app.optimistic-lock-max-attempts}",
            backoff = @Backoff(delayExpression = "${app.optimistic-lock-backoff-delay}")
    )
    @Override
    @Transactional
    public AccountResponseDto blockAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setAccountStatus(AccountStatus.BLOCKED);
        log.info("Account with number {} blocked", accountNumber);
        return accountMapper.toAccountResponseDto(account);
    }

    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${app.optimistic-lock-max-attempts}",
            backoff = @Backoff(delayExpression = "${app.optimistic-lock-backoff-delay}")
    )
    @Override
    @Transactional
    public AccountResponseDto closeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setAccountStatus(AccountStatus.CLOSED);
        log.info("Account with number {} closed", accountNumber);
        return accountMapper.toAccountResponseDto(account);
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND.formatted(accountNumber)));
    }

    private String generateAccountNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int length = secureRandom.nextInt(9) + 12;
        StringBuilder accountNumber = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            accountNumber.append(secureRandom.nextInt(10));
        }
        return accountNumber.toString();
    }
}
