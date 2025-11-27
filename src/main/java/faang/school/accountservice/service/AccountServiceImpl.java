package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto createAccount(CreateAccountDto createAccountDto) {
        Account account = accountMapper.toAccount(createAccountDto);
        account.setNumber(accountNumberGenerator.generateNumber());
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toAccountDto(savedAccount);
    }

    @Override
    public AccountDto getByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        return accountMapper.toAccountDto(account);
    }

    @Override
    @Transactional
    public void blockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (account.getStatus() == AccountStatusType.CLOSED) {
            throw new IllegalArgumentException("Cannot block a closed account");
        }

        account.setStatus(AccountStatusType.FROZEN);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.setStatus(AccountStatusType.CLOSED);
        account.setClosedAt(LocalDateTime.now());
    }
}
