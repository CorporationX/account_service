package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberService accountNumberService;

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);

        String accountNumber = accountNumberService.getAccountNumber();
        account.setAccountNumber(accountNumber);

        account = accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto freezeAccount(UUID id) {
        Account account = getValidAccount(id);
        account.setStatus(AccountStatus.FROZEN);
        account = accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto closeAccount(UUID id) {
        Account account = getValidAccount(id);
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        account = accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(UUID id) {
        Account account = getValidAccount(id);
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    private Account getValidAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found!"));
    }
}
