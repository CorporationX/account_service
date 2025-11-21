package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.ChangeAccountStatusReasonDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final UserContext userContext;
    private final AccountRepository accountRepository;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional(readOnly = true)
    public AccountDto getMyAccount(UUID accountId) {
        long userId = userContext.getUserId();
        Account account = accountRepository.findAccountByIdOrThrow(accountId);
        validateAccountOwner(account, userId);
        return AccountMapper.toDto(account);
    }

    @Transactional
    public AccountDto openAccount(AccountCreateDto accountCreateDto) {
        long userId = userContext.getUserId();
        String accountNumber = freeAccountNumbersService.generateAccountNumber();

        Account account = AccountMapper.toEntity(accountCreateDto);
        account.setStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(accountNumber);
        account.setUserId(userId);

        Account savedAccount = accountRepository.save(account);

        return AccountMapper.toDto(savedAccount);
    }

    @Transactional
    public AccountDto blockAccount(UUID accountId, ChangeAccountStatusReasonDto dto) {
        long userId = userContext.getUserId();
        Account account = accountRepository.findAccountByIdOrThrow(accountId);
        validateAccountOwner(account, userId);

        changeAccountStatus(account, AccountStatus.BLOCKED, dto.reason());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    @Transactional
    public AccountDto freezeAccount(UUID accountId, ChangeAccountStatusReasonDto dto) {
        long userId = userContext.getUserId();
        Account account = accountRepository.findAccountByIdOrThrow(accountId);
        validateAccountOwner(account, userId);

        changeAccountStatus(account, AccountStatus.FROZEN, dto.reason());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    @Transactional
    public AccountDto closeAccount(UUID accountId, ChangeAccountStatusReasonDto dto) {
        long userId = userContext.getUserId();
        Account account = accountRepository.findAccountByIdOrThrow(accountId);
        validateAccountOwner(account, userId);

        changeAccountStatus(account, AccountStatus.CLOSED, dto.reason());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    private void validateAccountOwner(Account account, long userId) {
        if (!ObjectUtils.nullSafeEquals(account.getUserId(), userId)) {
            throw new ForbiddenException("User %d is not the owner of account %s".formatted(userId, account.getId()));
        }
    }

    private void changeAccountStatus(Account account, AccountStatus newStatus, String reason) {
        if (account.getStatus() == newStatus) {
            throw new DataValidationException(
                    String.format("Account %s is already %s", account.getId(), newStatus.name()));
        }

        account.setStatus(newStatus);
        account.setStatusChangeReason(reason);
    }
}
