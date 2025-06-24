package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.exceptions.InsufficientFundsException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;

    public AccountDto getAccount(Long ownerId) {
        return repository.findById(ownerId).map(mapper::toDto).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
    }

    @Transactional
    public AccountDto openAccount(AccountDto dto) {
        Account newAccount = mapper.toEntity(dto);
        newAccount.setStatus(Account.Status.ACTIVE);
        Account savedAccount = repository.save(newAccount);
        return mapper.toDto(savedAccount);
    }

    @Transactional
    public AccountDto blockAccount(Long ownerId) {
        Account account = repository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
        account.setStatus(Account.Status.FROZEN);
        repository.save(account);
        return mapper.toDto(account);
    }

    @Transactional
    public AccountDto closeAccount(Long ownerId) {
        Account account = repository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
        account.setStatus(Account.Status.CLOSED);
        repository.save(account);
        return mapper.toDto(account);
    }

    @Transactional
    public void addBalance(Long ownerId, BigDecimal amount) {
        Account account = repository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
        account.setBalance(account.getBalance().add(amount));
        repository.save(account);
    }

    @Transactional
    public void spendBalance(Long ownerId, BigDecimal amount) {
        Account account = repository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
        BigDecimal newBalance = account.getBalance().subtract(amount);

        if(newBalance.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new InsufficientFundsException("Not enough money to withdraw");
        }

        account.setBalance(account.getBalance().subtract(amount));
        repository.save(account);
    }
}
