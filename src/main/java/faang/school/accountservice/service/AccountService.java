package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
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

    @Transactional
    public AccountDto findByAccountNumber(String number) {
        return accountMapper.toDto(accountRepository.findByNumber(number));
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        //Проверить что пользователь существует
        //Если это счет для физ лиц, то проверить user_id, если для юридических лиц то project_id
        Account account = accountMapper.toEntity(accountDto);
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
        Account account = accountRepository.findByNumber(number);
        account.setBalance(account.getBalance().add(summa));
        return accountMapper.toDto(accountJpaRepository.save(account));
    }

    @Transactional
    public AccountDto writeOff(String number, BigDecimal summa) {
        Account account = accountRepository.findByNumber(number);
        accountValidator.validateBalance(number, summa, account.getBalance());
        account.setBalance(account.getBalance().add(summa));
        return accountMapper.toDto(accountJpaRepository.save(account));
    }
}
