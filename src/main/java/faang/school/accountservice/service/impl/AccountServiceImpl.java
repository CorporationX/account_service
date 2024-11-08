package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto getAccountById(long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id " + id));
    }

    @Override
    public AccountDto openAccount(AccountDto accountDto) {
        accountRepository.save(accountMapper.toEntity(accountDto));
        return accountDto;
    }

    @Override
    public AccountDto frozeAccount(long id) {
        return updateAccountStatus(id, AccountStatus.FROZEN);
    }

    @Override
    public AccountDto blockAccount(long id) {
        return updateAccountStatus(id, AccountStatus.BLOCK);
    }

    @Override
    public AccountDto closeAccount(long id) {
        return updateAccountStatus(id, AccountStatus.CLOSED);
    }

    private AccountDto updateAccountStatus(long id, AccountStatus newStatus) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id " + id));
        account.setStatus(newStatus);
        return accountMapper.toDto(accountRepository.save(account));
    }
}
