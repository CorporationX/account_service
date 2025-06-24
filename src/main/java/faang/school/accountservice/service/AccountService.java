package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        account.setClosedAt(LocalDateTime.now());
        repository.save(account);
        return mapper.toDto(account);
    }
}
