package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.utils.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Override
    public AccountDto open(AccountDto accountDto) {
        String number;
        do {
            number = PasswordGeneratorUtil.generatePassword();
        } while (accountRepository.findByAccountNumber(number).isPresent());

        Account account = accountMapper.toEntity(accountDto);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber(number);

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    public AccountDto get(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void close(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        account.close();
    }

    @Override
    @Transactional
    public void block(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        account.block();
    }

    private Account getAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException(
                            "Account not found: " + accountNumber);
                });
    }
}
