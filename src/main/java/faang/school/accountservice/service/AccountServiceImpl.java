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
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountRepository.save(accountMapper.toEntity(accountDto));

        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountDto updateAccount(long accountId, UpdateAccountDto updateAccountDto) {
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
