package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.jpa.AccountJpaRepository;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional
    public AccountDto findByAccountNumber(String number) {
        return accountMapper.toDto(accountRepository.findByNumber(number));
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        accountValidator.accountOwnerValidate(accountDto);
        Account account = accountMapper.toEntity(accountDto);

        String accountNumber = freeAccountNumbersService.getFreeNumber(account.getType());
        account.setNumber(accountNumber);

        return accountMapper.toDto(accountJpaRepository.save(account));
    }

    @Transactional
    public AccountDto freezeAccount(String number) {
        Account account = accountRepository.findByNumber(number);
        account.setStatus(AccountStatus.FROZEN);
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto closeAccount(String number) {
        Account account = accountRepository.findByNumber(number);
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto deposit(String number, BigDecimal summa) {
        log.info("Deposit account: {}, summa: {}", number, summa);
        Account account = accountRepository.findByNumber(number);
        accountValidator.accountStatusValidate(account.getStatus());

        account.setBalance(account.getBalance().add(summa));
        return accountMapper.toDto(accountJpaRepository.save(account));
    }

    @Transactional
    public AccountDto writeOff(String number, BigDecimal summa) {
        log.info("WriteOff account: {}, summa: {}", number, summa);
        Account account = accountRepository.findByNumber(number);
        accountValidator.accountStatusValidate(account.getStatus());
        accountValidator.accountBalanceValidate(number, summa, account.getBalance());
        account.setBalance(account.getBalance().subtract(summa));
        return accountMapper.toDto(accountJpaRepository.save(account));
    }
}
