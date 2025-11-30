package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
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
    private final OwnerRepository ownerRepository;
    private final OperationValidator operationValidator;


    @Override
    public AccountDto getById(Long id) {
        return accountMapper.mapToDto(accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account with id %d not found", id))));
    }

    @Override
    public AccountDto getByAccountNumber(String accountNumber) {
        Account account = operationValidator.validateAccount(accountNumber);
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

        Owner owner = ownerRepository.upsertOwner(accountDto.ownerType(), accountDto.ownerId());
        account.setOwner(owner);
        account = accountRepository.save(account);

        log.info("Account with id {} created", account.getId());
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto blockAccount(String accountNumber) {
        Account account = operationValidator.validateAccountActive(accountNumber);
        account.setAccountStatus(AccountStatus.FROZEN);
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto unblockAccount(String accountNumber) {
        Account account = operationValidator.validateAccountFrozen(accountNumber);
        account.setAccountStatus(AccountStatus.ACTIVE);
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto closeAccount(String accountNumber) {
        Account account = operationValidator.validateAccountReadyToClose(accountNumber);
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto withdraw(String accountNumber, BigDecimal amount) {
        Account account = operationValidator.validateAccountActive(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            log.error("Account with number {} has insufficient balance", accountNumber);
            throw new ForbiddenException(
                    String.format("Account with number %s has insufficient balance", accountNumber));
        }
        account.setBalance(account.getBalance().subtract(amount));
        log.info("Account with number {} withdrawn {}", accountNumber, amount);
        return accountMapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountDto deposit(String accountNumber, BigDecimal amount) {
        Account account = operationValidator.validateAccountActive(accountNumber);

        account.setBalance(account.getBalance().add(amount));
        log.info("Account with number {} deposited {}", accountNumber, amount);

        return accountMapper.mapToDto(account);
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        Account account = operationValidator.validateAccountActive(accountNumber);
        return account.getBalance();
    }

    @Override
    public List<AccountDto> getAccountsByOwner(Long personId, OwnerType ownerType) {
        return accountMapper.mapToDtos(accountRepository.findByPersonIdAndOwnerType(personId, ownerType));
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