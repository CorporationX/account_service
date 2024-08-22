package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;

    @Transactional
    public List<AccountDto> getAllAccounts(long ownerId) {
        List<Account> accounts = accountRepository.findAllByOwnerId(ownerId);
        return accounts.stream().map(accountMapper::toDto).toList();
    }

    @Transactional
    public AccountDto getAccount(String number) {
        return accountMapper.toDto(findAccountAndValid(number));
    }

    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);

        accountValidator.checkExistenceOfTheNumber(account);
        accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto updateStatusAccount(String number, String status) {
        Account account = findAccountAndValid(number);

        AccountStatus newStatus = accountValidator.checkForMatchStatusAndTransformation(status);

        if (!account.getAccountStatus().equals(newStatus)) {
            account.setAccountStatus(newStatus);
            if (newStatus == AccountStatus.CLOSED) {
                account.setCloseAt(LocalDateTime.now());
            }
            accountRepository.save(account);
            log.info("Статус аккаунта с номером {} был изменён на {}", number, newStatus);
        }

        return accountMapper.toDto(account);
    }

    private Account findAccountAndValid(String number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> {
                    log.error("Аккаунта по такому номеру {} не существует", number);
                    return new IllegalArgumentException("Аккаунта по такому номеру не существует");
                });
    }
}