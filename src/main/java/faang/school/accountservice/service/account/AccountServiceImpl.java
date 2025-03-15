package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountGenerator;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponseDto get(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("There is no account with id = %d in the database".formatted(id)));
        return accountMapper.toAccountResponseDto(account);
    }

    @Override
    public AccountResponseDto open(AccountRequestDto accountDto) {
        Account account = accountMapper.toAccountEntity(accountDto);
        account.setAccount(accountGenerator.generateUniqueAccountNumber());
        return accountMapper.toAccountResponseDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}")
    )
    public void deactivate(Long id, AccountStatus accountStatus) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("There is no account with id = %d in the database".formatted(id)));
        account.setAccountStatus(accountStatus);
    }

    @Recover
    public void recoverDeactivate(OptimisticLockingFailureException e, Long id, AccountStatus accountStatus) {
        log.error("Failed to deactivate account with id = {} after multiple attempts", id, e);
        throw new RuntimeException("Failed to deactivate account due to concurrent modification", e);
    }
}
