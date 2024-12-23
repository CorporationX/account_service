package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.ConflictException;
import faang.school.accountservice.mappers.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto getAccount(Long accountId) {
        validateAccountId(accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found")
        );

        return accountMapper.toDto(account);
    }

    public AccountDto openAccount(AccountDto accountDto) {
        if (accountRepository.existsById(accountDto.getId())) {
            throw new IllegalArgumentException("Account with ID " + accountDto.getId() + " already exists");
        }

        if (accountRepository.existsByNumber(accountDto.getNumber())) {
            throw new ConflictException("Account with number " + accountDto.getNumber() + " already exists");
        }

        Account account = accountMapper.toEntity(accountDto);
        account.setCreatedAt(LocalDateTime.now());

        try {
            Account savedAccount = accountRepository.save(account);

            return accountMapper.toDto(savedAccount);

        } catch (OptimisticLockException e) {
            throw new ConflictException("The account has been updated by another process.");
        }
    }

    public AccountDto blockAccount(Long accountId) {
        validateAccountId(accountId);

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account not found"));


        try {
            account.setStatus(AccountStatus.BLOCKED);
            account.setUpdatedAt(LocalDateTime.now());
            account.setVersion(account.getVersion() + 1);
            return accountMapper.toDto(accountRepository.save(account));

        } catch (OptimisticLockException e) {
            throw new ConflictException("The account has been updated by another process.");
        }

    }

    public AccountDto closeAccount(Long accountId) {
        validateAccountId(accountId);

        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account not found"));

        try {
            account.setStatus(AccountStatus.CLOSED);
            account.setClosedAt(LocalDateTime.now());
            account.setVersion(account.getVersion() + 1);
            return accountMapper.toDto(accountRepository.save(account));

        } catch (OptimisticLockException e) {
            throw new ConflictException("The account has been updated by another process.");
        }
    }

    public AccountDto getAccountByNumber(String number){
        Account account = accountRepository.findByNumber(number);
        if (account.getStatus() == AccountStatus.BLOCKED || account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account has been blocked");
        }

        return accountMapper.toDto(account);
    }


    private void validateAccountId(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        if (accountId < 0) {
            throw new IllegalArgumentException("Account ID cannot be less than zero");
        }
    }
}
