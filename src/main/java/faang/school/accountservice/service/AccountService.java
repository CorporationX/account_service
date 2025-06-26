package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
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

    public AccountDto getAccountDto(Long id) {
        return mapper.toDto(getAccount(id));
    }

    private Account getAccount(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nonexistent id"));
    }

    @Transactional
    public AccountDto openAccount(AccountDto dto) {
        Account newAccount = Account.builder()
                .id(dto.id())
                .ownerType(dto.ownerType())
                .ownerId(dto.ownerId())
                .type(dto.type())
                .currency(dto.currency())
                .status(Status.ACTIVE)
                .build();
        Account savedAccount = repository.save(newAccount);
        return mapper.toDto(savedAccount);
    }

    @Transactional
    public AccountDto blockAccount(Long id) {
        Account account = getAccount(id);
        account.setStatus(Status.FROZEN);
        repository.save(account);
        return mapper.toDto(account);
    }

    @Transactional
    public AccountDto closeAccount(Long id) {
        Account account = getAccount(id);
        account.setStatus(Status.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        repository.save(account);
        return mapper.toDto(account);
    }
}
