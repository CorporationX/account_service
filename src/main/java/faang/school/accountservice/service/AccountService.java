package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.exeption.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new DataValidationException("Account with ID = " + id + " not found"));
        log.info("Account with ID = {} found", id);
        return accountMapper.toDto(account);
    }

    public AccountDto getAccountByNumber(String number) {
        Account account = accountRepository.findByNumber(number).orElseThrow(() ->
                new DataValidationException("Account with number = " + number + " not found"));
        log.info("Account with number = {} found", number);
        return accountMapper.toDto(account);
    }

    public AccountDto openAccount(OpenAccountDto openAccountDto) {
        Account account = accountMapper.toEntity(openAccountDto);
        Account openAccount = accountRepository.save(account);
        log.info("Account with number = {} opened", openAccountDto.getNumber());
        return accountMapper.toDto(openAccount);
    }

    public AccountDto blockAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new DataValidationException("Account with ID = " + id + " not found"));
        account.setStatus(Status.FROZEN);
        Account blockAccount = accountRepository.save(account);
        log.info("Account with ID = {} blocked", id);
        return accountMapper.toDto(blockAccount);
    }

    public AccountDto closeAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new DataValidationException("Account with ID = " + id + " not found"));
        account.setStatus(Status.CLOSED);
        Account closeAccount = accountRepository.save(account);
        log.info("Account with ID = {} closed", id);
        return accountMapper.toDto(closeAccount);
    }
}
