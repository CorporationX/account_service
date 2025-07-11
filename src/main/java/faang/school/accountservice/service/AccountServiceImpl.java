package faang.school.accountservice.service;

import faang.school.accountservice.account.strategy.AccountTypeSpecifiedAction;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStateException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.utils.AccountNumberGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final FreeAccountNumbersServiceImpl freeAccountNumbersService;
    private final List<AccountTypeSpecifiedAction> accountTypeActions;

    @Transactional
    @Override
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        account.setStatus(AccountStatus.ACTIVE);
        freeAccountNumbersService.acceptAccountNumber(account.getType(), number -> {
            account.setNumber(number.toString());
        });

        Optional<AccountTypeSpecifiedAction> accountTypeSpecifiedActionction = accountTypeActions.stream()
                .filter(action -> action.getAccountType() == account.getType())
                .findFirst();

        Account savedAccount;
        if (accountTypeSpecifiedActionction.isPresent()) {
            savedAccount = accountTypeSpecifiedActionction.get().createAccount(account);
        } else {
            savedAccount = accountRepository.save(account);
        }

        return accountMapper.toDto(savedAccount);
    }


    @Override
    public AccountDto getAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void closeAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException(
                    String.format("Account with AccountNumber:%s is already closed", accountNumber));
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void blockAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStateException(
                    String.format("Account with AccountNumber:%s is already closed", accountNumber));
        }
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountStateException(
                    String.format("Account with AccountNumber:%s is already blocked", accountNumber));
        }
        account.setStatus(AccountStatus.BLOCKED);

        accountRepository.save(account);
    }

    private Account getAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account with accountNumber: {} not found.", accountNumber);
                    return new AccountNotFoundException(
                            String.format("Account with accountNumber: %s not found.", accountNumber));
                });
    }
}
