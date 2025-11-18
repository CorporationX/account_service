package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountReasonDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final UserContext userContext;
    private final AccountRepository accountRepository;

    public AccountDto getMyAccount(Long accountId) {
        long userId = userContext.getUserId();
        Account account = accountRepository.getByIdAndUserIdOrThrow(accountId, userId);
        return AccountMapper.toDto(account);
    }

    public AccountDto openAccount(AccountCreateDto accountCreateDto) {
        long userId = userContext.getUserId();
        String accountNumber = generateAccountNumber();

        Account account = AccountMapper.toEntity(accountCreateDto);
        account.setStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(accountNumber);
        account.setUserId(userId);

        Account savedAccount = accountRepository.save(account);

        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto blockAccount(Long accountId, AccountReasonDto dto) {
        long userId = userContext.getUserId();
        Account account = accountRepository.getByIdAndUserIdOrThrow(accountId, userId);
        changeAccountStatus(account, AccountStatus.BLOCKED, dto.reason());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto freezeAccount(Long accountId, AccountReasonDto dto) {
        long userId = userContext.getUserId();
        Account account = accountRepository.getByIdAndUserIdOrThrow(accountId, userId);
        changeAccountStatus(account, AccountStatus.FROZEN, dto.reason());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto closeAccount(Long accountId, AccountReasonDto dto) {
        long userId = userContext.getUserId();
        Account account = accountRepository.getByIdAndUserIdOrThrow(accountId, userId);
        changeAccountStatus(account, AccountStatus.CLOSED, dto.reason());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    private void changeAccountStatus(Account account, AccountStatus newStatus, String reason) {
        if (account.getStatus() == newStatus) {
            throw new DataValidationException(
                    String.format("Account %d is already %s", account.getId(), newStatus.name().toLowerCase()));
        }

        account.setStatus(newStatus);
        clearAllStatusFields(account);

        switch (newStatus) {
            case BLOCKED -> {
                account.setBlockedAt(LocalDateTime.now());
                account.setBlockReason(reason);
            }
            case FROZEN -> {
                account.setFrozenAt(LocalDateTime.now());
                account.setFrozenReason(reason);
            }
            case CLOSED -> {
                account.setClosedAt(LocalDateTime.now());
                account.setCloseReason(reason);
            }
        }
    }

    private void clearAllStatusFields(Account account) {
        account.setBlockedAt(null);
        account.setBlockReason(null);
        account.setFrozenAt(null);
        account.setFrozenReason(null);
        account.setClosedAt(null);
        account.setCloseReason(null);
    }

    //todo: временно решение, пока не сделана задача "Генерация уникальных номеров счетов"
    private String generateAccountNumber() {
        Random random = new Random();

        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

}
