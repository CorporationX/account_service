package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Override
    public AccountDto open(AccountDto accountDto) {
        Account account = accountRepository.save(accountMapper.toEntity(accountDto));

        return accountMapper.toDto(account);
    }

    @Override
    public AccountDto get(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No account was found with such number"));

        return accountMapper.toDto(account);
    }

    @Override
    public void close(String accountNumber) {
        int closed = accountRepository.closeAccountByNumber(accountNumber);
        if (closed == 0) {
            throw new IllegalArgumentException("No account was closed as no such number was found");
        }
    }

    @Override
    public void block(String accountNumber) {
        int blocked = accountRepository.blockAccountByNumber(accountNumber);
        if (blocked == 0) {
            throw new IllegalArgumentException("No account was blocked as no such number was found");
        }
    }

    @Override
    @Transactional
    public AccountDto update(long accountId, UpdateAccountDto updateAccountDto) {
        Account account = accountRepository.getReferenceById(accountId);
        accountMapper.update(updateAccountDto, account);

        try {
            return accountMapper.toDto(accountRepository.save(account));
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock happened try again.");
            return accountMapper.toDto(account);
        }
    }
}
