package faang.school.accountservice.service.account;

import faang.school.accountservice.config.generator.AccountNumberGenerator;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.IllegalStatusException;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.owner.OwnerService;
import faang.school.accountservice.service.status.AccountStatusManager;
import faang.school.accountservice.service.type.TypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountStatusManager accountStatusManager;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OwnerService ownerService;
    private final TypeService typeService;

    @Transactional(readOnly = true)
    public List<AccountDto> getAccounts() {
        log.debug("getAccounts() - start");

        List<Account> accounts = accountRepository.findAll();
        List<AccountDto> accountDtos = accountMapper.toAccountDtos(accounts);

        log.debug("getAccounts() - finish");
        return accountDtos;
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountByAccountId(long accountId) {
        log.info("getAccountByAccountId() - start, accountId - {}", accountId);

        Account account = getAccount(accountId);
        AccountDto accountDto = accountMapper.toAccountDto(account);

        log.info("getAccountByAccountId() - finish, accountDto - {}", accountDto);
        return accountDto;
    }

    @Transactional
    public AccountDto createAccount(AccountCreateDto accountCreateDto) {
        log.info("createAccount() - start, accountCreateDto - {}", accountCreateDto);
        Account account = generateNewAccount(accountCreateDto);

        account = accountRepository.save(account);
        AccountDto accountDto = accountMapper.toAccountDto(account);
        log.info("createAccount() - finish, accountDto - {}", accountDto);
        return accountDto;
    }

    @Transactional
    public AccountDto blockAccount(long accountId) {
        log.info("blockAccount() - start, accountId - {}", accountId);
        Account account = getAccount(accountId);

        updateAccountStatusIfAvailable(account, AccountStatus.FROZEN);

        account = accountRepository.save(account);
        AccountDto accountDto = accountMapper.toAccountDto(account);
        log.info("blockAccount() - finish, accountDto - {}", accountDto);
        return accountDto;
    }

    @Transactional
    public void closeAccount(long accountId) {
        log.info("closeAccount() - start, accountId - {}", accountId);
        Account account = getAccount(accountId);

        updateAccountStatusIfAvailable(account, AccountStatus.CLOSED);

        account.setClosedAt(LocalDateTime.now());
        account = accountRepository.save(account);
        log.info("closeAccount() - finish, account - {}", account);
    }

    public Account getAccount(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + accountId + " not found"));
    }

    private void updateAccountStatusIfAvailable(Account account, AccountStatus statusForUpdate) {
        if (accountStatusManager.isStatusAvailableForChange(account.getStatus(), statusForUpdate)) {
            account.setStatus(statusForUpdate);
        } else {
            throw new IllegalStatusException(statusForUpdate + " in not available for account");
        }
    }

    private Account generateNewAccount(AccountCreateDto accountCreateDto) {
        log.debug("generateNewAccount - start, accountDto - {}", accountCreateDto);
        AccountType type = typeService.getTypeByName(accountCreateDto.getType().getName());
        Owner owner = ownerService.getOwnerByName(accountCreateDto.getOwner().getName());

        String accountNumber = accountNumberGenerator.generateRandomAccountNumberInRange();

        Account account = Account.builder()
                .number(accountNumber)
                .accountType(type)
                .owner(owner)
                .currency(accountCreateDto.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();

        log.debug("generateNewAccount - finish, account - {}", account);
        return account;
    }
}