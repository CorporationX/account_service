package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceAuditDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Operation;
import faang.school.accountservice.jpa.AccountJpaRepository;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final BalanceAuditMapper balanceAuditMapper;
    private final BalanceAuditRepository balanceAuditRepository;

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
        Balance balance = Balance.builder()
                .account(account)
                .build();

        balance.setAccount(account);
        account.setBalance(balance);

        AccountDto savedAccountDto = accountMapper.toDto(accountJpaRepository.save(account));

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);
        balanceAudit.setOperation(Operation.OPEN);
        balanceAuditRepository.save(balanceAudit);

        return savedAccountDto;
    }

    @Transactional
    public AccountDto freezeAccount(String number) {
        Account account = accountRepository.findByNumber(number);
        account.setStatus(AccountStatus.FROZEN);

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(account.getBalance());
        balanceAudit.setOperation(Operation.FREEZE);
        balanceAudit.setCreatedAt(LocalDateTime.now());
        balanceAuditRepository.save(balanceAudit);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto closeAccount(String number) {
        LocalDateTime now = LocalDateTime.now();
        Account account = accountRepository.findByNumber(number);
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(now);

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(account.getBalance());
        balanceAudit.setOperation(Operation.CLOSE);
        balanceAudit.setCreatedAt(now);
        balanceAuditRepository.save(balanceAudit);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto deposit(String number, BigDecimal summa) {
        log.info("Deposit account: {}, summa: {}", number, summa);
        Account account = accountRepository.findByNumber(number);
        accountValidator.accountStatusValidate(account.getStatus());
        BigDecimal actualBalance = account.getBalance().getActualBalance();
        account.getBalance().setActualBalance(actualBalance.add(summa));

        account = accountJpaRepository.save(account);
        AccountDto accountDto = accountMapper.toDto(account);

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(account.getBalance());
        balanceAudit.setOperation(Operation.DEPOSIT);
        balanceAudit.setCreatedAt(account.getBalance().getUpdatedAt());
        balanceAuditRepository.save(balanceAudit);

        return accountDto;
    }

    @Transactional
    public AccountDto writeOff(String number, BigDecimal summa) {
        log.info("WriteOff account: {}, summa: {}", number, summa);
        Account account = accountRepository.findByNumber(number);
        accountValidator.accountStatusValidate(account.getStatus());
        BigDecimal actualBalance = account.getBalance().getActualBalance();
        accountValidator.accountBalanceValidate(number, summa, actualBalance);
        account.getBalance().setActualBalance(actualBalance.subtract(summa));

        account = accountJpaRepository.save(account);
        AccountDto accountDto = accountMapper.toDto(account);

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(account.getBalance());
        balanceAudit.setOperation(Operation.WRITE_OFF);
        balanceAudit.setCreatedAt(account.getBalance().getUpdatedAt());
        balanceAuditRepository.save(balanceAudit);

        return accountDto;
    }

    public List<BalanceAuditDto> getAccountBalanceAudit(long accountId) {
        return balanceAuditMapper.toListDto(balanceAuditRepository.findByAccountId(accountId));
    }
}
