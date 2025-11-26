package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.model.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto getById(Long id) {
        return accountMapper.mapToDto(accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found")));
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

        //в дальнейшем будут генерироваться уникальные номера
        account.setAccountNumber(generateAccountNumber());
        account = accountRepository.save(account);
        Owner owner = Owner.builder()
                .personId(accountDto.ownerId())
                .ownerType(accountDto.ownerType())
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
        if (!account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new ForbiddenException("Account is not active");
        }
        account.setAccountStatus(AccountStatus.FROZEN);
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto unblockAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        if (!account.getAccountStatus().equals(AccountStatus.FROZEN)) {
            throw new ForbiddenException("Account is not frozen");
        }
        account.setAccountStatus(AccountStatus.ACTIVE);
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto closeAccount(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        if (account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            log.error("Account with number {} is already closed", accountNumber);
            throw new ForbiddenException("Account is already closed");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ForbiddenException(String.format("Account with number %s has a non-zero balance", accountNumber));
        }
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto withdraw(String accountNumber, BigDecimal amount) {
        Account account = validateAccountNumber(accountNumber);
        BigDecimal currentBalance = account.getBalance();
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)
                && currentBalance.compareTo(amount) > -1) {
            account.setBalance(currentBalance.subtract(amount));
            log.info("Account with number {} withdrawn {}", accountNumber, amount);
        } else {
            throw new ForbiddenException(
                    String.format("Account with number %s is not active or balance is insufficient", accountNumber));
        }
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto deposit(String accountNumber, BigDecimal amount) {
        Account account = validateAccountNumber(accountNumber);
        BigDecimal currentBalance = account.getBalance();
        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            account.setBalance(currentBalance.add(amount));
            log.info("Account with number {} deposited {}", accountNumber, amount);
        } else {
            throw new ForbiddenException(String.format("Account with number %s is not active", accountNumber));
        }
        return accountMapper.mapToDto(account);
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        Account account = validateAccountNumber(accountNumber);
        if (account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            throw new ForbiddenException(String.format("Account with number %s is closed", accountNumber));
        }
        return account.getBalance();
    }

    @Override
    public List<AccountDto> getAccountsByOwner(Long personId, OwnerType ownerType) {
        return accountMapper.mapToDtos(accountRepository.findByPersonIdAndOwnerType(personId, ownerType));
    }

    private Account validateAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Account with number %s not found", accountNumber)));
    }

    //временный метод заглушка
    private String generateAccountNumber() {
        String prefix = "408";
        StringBuilder base = new StringBuilder(prefix);
        for (int i = 0; i < 17; i++) {
            base.append(new Random().nextInt(10));
        }
        return base.toString();
    }
}