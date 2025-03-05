package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository repository;
    private final AccountMapper accountMapper;

    @Override
    public void createAccount(AccountDto dto) {
        log.info("Creating account {}", dto);
        repository.save(accountMapper.toEntity(dto));
    }

    @Override
    public AccountDto findAccountById(Long id) {
        log.info("Finding account by id {}", id);

        return accountMapper.toDto(getAccount(id));
    }

    private Account getAccount(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account with id %s not found", id)));
    }

    @Override
    public void deleteAccountById(Long id) {
        log.info("Deleting account by id {}", id);
        repository.deleteById(id);
    }

    @Override
    public AccountDto updateAccount(AccountDto dto) {
        log.info("Updating account {}", dto);
        Account account = getAccount(dto.getId());
        accountMapper.updateAccount(account, dto);

        return accountMapper.toDto(repository.save(account));
    }
}
