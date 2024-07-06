package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final FreeAccountNumberService freeAccountNumberService;

    @Transactional
    public AccountDto open(AccountDto accountDto) {
        BigInteger accountNumber = freeAccountNumberService.getFreeNumber(accountDto.getAccountType());
        Account account = accountMapper.toEntity(accountDto);
        Balance balance = new Balance();
        balance.setCurrentBalance(BigDecimal.ZERO);
        account.setBalance(balance);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setNumber(accountNumber);
        Account savedAccount = accountRepository.save(account);
        savedAccount.getBalance().setAccount(account);
        return accountMapper.toDto(accountRepository.save(savedAccount));
    }

    public AccountDto get(long accountId) {
        return accountMapper.toDto(findAccountEntityById(accountId));
    }

    public AccountDto getByNumber(BigInteger number) {
        return accountMapper.toDto(findAccountEntityByNumber(number));
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000, multiplier = 2.0))
    public void block(long accountId) {
        Account account = findAccountEntityById(accountId);
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000, multiplier = 2.0))
    public void close(long accountId) {
        Account account = findAccountEntityById(accountId);
        account.setAccountStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    public boolean existsByNumber(BigInteger number) {
        return accountRepository.existsAccountByNumber(number);
    }

    private Account findAccountEntityById(long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("account with id = %d not found", id)));
    }

    private Account findAccountEntityByNumber(BigInteger number) {
        return accountRepository.findByNumber(number).orElseThrow(
                () -> new EntityNotFoundException(String.format("account with number = %s not found", number)));
    }
}