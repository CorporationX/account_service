package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountUpdateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper mapper;
    private final AccountRepository repository;

    @Override
    @Transactional
    public AccountViewDto openAccount(AccountCreateDto createDto) {
        Account createAccount = mapper.toEntity(createDto);

        Long min = 100_000_000_000L;
        Long max = 100_000_000_000_000_000L;
        Long randomNum = min + (long) (new SecureRandom().nextDouble() * (max - min + 1));

        createAccount.setAccountNumber(String.valueOf(randomNum));
        createAccount.setStatus(AccountStatus.ACTIVE);

        repository.save(createAccount);
        return mapper.toViewDto(createAccount);
    }

    @Override
    @Transactional
    public AccountViewDto getAccount(Long id) {
        Account account = repository.findByIdOrThrow(id);
        return mapper.toViewDto(account);
    }

    @Override
    @Transactional
    @Retryable(retryFor = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2))
    public AccountViewDto blockAccount(Long id, AccountUpdateDto updateDto) {
        Account account = repository.findByIdOrThrow(id);
        if (account.getStatus().equals(AccountStatus.CLOSED)) {
            throw new ForbiddenException(String.format("Счет %s уже был закрыт.", account.getId()));
        }
        mapper.update(updateDto, account);
        Account updatedAccount = repository.save(account);
        return mapper.toViewDto(updatedAccount);
    }

    @Override
    @Transactional
    @Retryable(retryFor = {ObjectOptimisticLockingFailureException.class}, maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public AccountViewDto closeAccount(Long id) {
        Account account = repository.findByIdOrThrow(id);
        if (account.getStatus().equals(AccountStatus.CLOSED)) {
            throw new ForbiddenException(String.format("Счет %s уже был закрыт.", account.getId()));
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        Account closedAccount = repository.save(account);
        return mapper.toViewDto(closedAccount);
    }
}
