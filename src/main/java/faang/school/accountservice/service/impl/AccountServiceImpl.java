package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import faang.school.accountservice.util.ExceptionThrowingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final FreeAccountNumbersServiceImpl freeAccountNumbersServiceImpl;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final ExceptionThrowingValidator validator;
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional(readOnly = true)
    @Override
    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDto getAccountNumber(String number) {
        Account account = accountRepository.findAccountByNumber(number).orElseThrow();

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional
    @Override
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.accountDtoToAccount(accountDto);
        Consumer<FreeAccountNumber> consumer = freeAccountNumber -> {
            account.setNumber(freeAccountNumber.getId().getNumber());
            accountRepository.save(account);
        };

        Balance balance = new Balance();
        balance.setAccount(account);
        account.setBalance(balance);
        freeAccountNumbersService.getFreeAccountNumber(account.getType(), consumer);
        AccountDto createdAccountDto = accountMapper.accountToAccountDto(accountRepository.save(account));
        createBalanceAudit(account.getBalance(), UUID.randomUUID());
        validator.validate(createdAccountDto, AccountDto.Created.class);
        return createdAccountDto;
    }

    @Transactional
    @Override
    public AccountDto blockAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        account.setStatus(AccountStatus.BLOCKED);

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional
    @Override
    public AccountDto blockAccountNumber(String number) {
        Account account = accountRepository.findAccountByNumber(number).orElseThrow();
        account.setStatus(AccountStatus.BLOCKED);

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional
    @Override
    public List<AccountDto> blockAllAccountsByUserOrProject(Long userId, Long projectId) {
        List<Account> accounts;
        if (userId != null) {
            accounts = accountRepository.findAllByUserId(userId);
        } else {
            accounts = accountRepository.findAllByProjectId(projectId);
        }
        if (!accounts.isEmpty()) {
            accounts.forEach(account -> account.setStatus(AccountStatus.BLOCKED));
        }

        return accountMapper.accountListToAccountDtoList(accounts);
    }

    @Transactional
    @Override
    public AccountDto unblockAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        account.setStatus(AccountStatus.ACTIVE);

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional
    @Override
    public AccountDto unblockAccountNumber(String number) {
        Account account = accountRepository.findAccountByNumber(number).orElseThrow();
        account.setStatus(AccountStatus.ACTIVE);

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional
    @Override
    public List<AccountDto> unblockAllAccountsByUserOrProject(Long userId, Long projectId) {
        List<Account> accounts;
        if (userId != null) {
            accounts = accountRepository.findAllByUserId(userId);
        } else {
            accounts = accountRepository.findAllByProjectId(projectId);
        }
        if (!accounts.isEmpty()) {
            accounts.forEach(account -> account.setStatus(AccountStatus.ACTIVE));
        }

        return accountMapper.accountListToAccountDtoList(accounts);
    }

    @Transactional
    @Override
    public AccountDto closeAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        account.setStatus(AccountStatus.CLOSED);

        return accountMapper.accountToAccountDto(account);
    }

    @Transactional
    @Override
    public AccountDto closeAccountNumber(String number) {
        Account account = accountRepository.findAccountByNumber(number).orElseThrow();
        account.setStatus(AccountStatus.CLOSED);

        return accountMapper.accountToAccountDto(account);
    }

    private void createBalanceAudit(Balance balance, UUID operationId) {
        BalanceAudit audit = new BalanceAudit();
        audit.setAccountNumber(balance.getAccount().getNumber());
        audit.setBalanceVersion(balance.getVersion().intValue());
        audit.setAuthorizedAmount(balance.getAuthorizedBalance());
        audit.setActualAmount(balance.getActualBalance());
        audit.setOperationId(operationId);
        audit.setCreatedAt(LocalDateTime.now());
        balanceAuditRepository.save(audit);;
    }
}