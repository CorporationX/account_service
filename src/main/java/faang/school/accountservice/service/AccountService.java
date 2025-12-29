package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto get(UUID id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    public AccountDto open(AccountDto request) {
        if (accountRepository.findByNumber(request.getNumber()).isPresent()) {
            throw new RuntimeException("Account number already exists");
        }
        Account account = accountMapper.toEntity(request);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setVersion(1);
        account = accountRepository.save(account);
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    public AccountDto block(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new RuntimeException("Account already frozen!");
        }
        account.setStatus(AccountStatus.FROZEN);
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto close(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus().equals(AccountStatus.CLOSED)) {
            throw new RuntimeException("Account already closed!");
        }
        account.setStatus(AccountStatus.CLOSED);
        return accountMapper.toDto(accountRepository.save(account));
    }
}