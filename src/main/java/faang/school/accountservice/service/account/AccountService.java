package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static faang.school.accountservice.exception.message.AccountExceptionMessage.NON_EXISTING_ACCOUNT_BY_ID_EXCEPTION;
import static faang.school.accountservice.exception.message.AccountExceptionMessage.NON_EXISTING_ACCOUNT_BY_NUMBER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountServiceValidator accountServiceValidator;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountDto open(AccountDto accountDto) {
        accountServiceValidator.validateOwnerExistence(accountDto.getOwnerUserId(), accountDto.getOwnerProjectId());

        Account accountToBeOpened = accountMapper.toEntity(accountDto);
        freeAccountNumbersService.consumeFreeNumber(accountDto.getType(), accountToBeOpened::setNumber);

        Account createdAccount = accountRepository.save(accountToBeOpened);
        return accountMapper.toDto(createdAccount);
    }

    public AccountDto getAccountById(Long accountId) {
        Account account = getAccountModelById(accountId);

        return accountMapper.toDto(account);
    }

    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = getAccountModelByNumber(accountNumber);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto changeStatus(Long accountId, AccountStatus status) {
        Account account = getAccountModelById(accountId);
        accountServiceValidator.validateStatusBeforeUpdate(account);

        if (status.equals(AccountStatus.CLOSED)) {
            account.setClosedAt(LocalDateTime.now());
        }
        account.setStatus(status);

        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional(readOnly = true)
    public Account getAccountModelById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    String message = String.format(NON_EXISTING_ACCOUNT_BY_ID_EXCEPTION.getMessage(), accountId);
                    return new NoSuchElementException(message);
                });
    }

    @Transactional(readOnly = true)
    public Account getAccountModelByNumber(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> {
                    String message = String.format(NON_EXISTING_ACCOUNT_BY_NUMBER_EXCEPTION.getMessage(), accountNumber);
                    return new NoSuchElementException(message);
                });
    }
}
