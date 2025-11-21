package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto getById(Long id) {
        return accountMapper.mapToDto(accountRepository.getByIdOrThrow(id));
    }

    @Override
    public AccountDto getByAccountNumber(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        log.info("Account with number {} found", accountNumber);

        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto openAccount(CreateAccountDto accountDto) {

        Account account = accountMapper.createAccountDtoToAccount(accountDto);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account = accountRepository.save(account);
        incrementAccountVersion(account);
        Owner owner = Owner.builder()
                .personId(accountDto.ownerId())
                .ownerPerson(accountDto.ownerPerson())
                .account(account)
                .build();
        account.setOwner(owner);

        log.info("Account with id {} created", account.getId());
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto blockAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.FROZEN);
        incrementAccountVersion(account);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountDto unblockAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.ACTIVE);
        incrementAccountVersion(account);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountDto closeAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.CLOSED);
        incrementAccountVersion(account);
        return accountMapper.mapToDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountDto withdraw(String accountNumber, double amount) {
        Account account = validateAccountNumber(accountNumber);
        double currentBalance = account.getBalance();
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)
                && currentBalance >= amount) {
            account.setBalance(currentBalance - amount);
            log.info("Account with number {} withdrawn {}", accountNumber, amount);
        } else {
            log.error("Account with number {} is not active or balance is insufficient", accountNumber);
            throw new ForbiddenException("Account is not active or balance is insufficient");
        }
        return accountMapper.mapToDto(account);
    }

    @Override
    public AccountDto deposit(String accountNumber, double amount) {
        Account account = validateAccountNumber(accountNumber);
        double currentBalance = account.getBalance();
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            account.setBalance(currentBalance + amount);
            log.info("Account with number {} deposited {}", accountNumber, amount);
        } else {
            log.error("Account with number {} is not active", accountNumber);
            throw new ForbiddenException("Account is not active");
        }
        return accountMapper.mapToDto(account);
    }

    @Override
    public Double getBalance(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        return account.getBalance();
    }

    private Account validateAccountNumber(String accountNumber) {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            log.error("Account with number {} not found", accountNumber);
            throw new EntityNotFoundException("Account not found");
        }
        return optionalAccount.get();
    }

    @Transactional
    private void incrementAccountVersion(Account account) {
        if (account.getAccountVersion() == null) {
            account.setAccountVersion(0L);
        }
        long version = account.getAccountVersion();
        account.setAccountVersion(++version);
        accountRepository.save(account);
        log.info("Account version incremented to {}", version);
    }

    private String generateAccountNumber() {
        String prefix = "408";
        StringBuilder base = new StringBuilder(prefix);
        for (int i = 0; i < 17; i++) {
            base.append(new Random().nextInt(10));
        }
        return base.toString();
    }
}