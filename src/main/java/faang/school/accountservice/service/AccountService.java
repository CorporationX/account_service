package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto getAccountByNumber(String number) {
        return accountMapper.toDto(findAccount(number));
    }

    public AccountDto openAccount(AccountDto accountDto) {
        var account = accountMapper.toEntity(accountDto);
        account.setCreatedAt(LocalDateTime.now());
        var savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    public void blockAccount(String number) {
        var account = findAccount(number);
        account.setStatus(AccountStatus.BLOCK);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Transactional
    public void closeAccount(String number) {
        var account = findAccount(number);
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private Account findAccount(String number){
        return accountRepository.findAccountByNumber(number)
                .orElseThrow(() -> new NoSuchElementException(format("Account with number %s is not found.", number)));
    }
}