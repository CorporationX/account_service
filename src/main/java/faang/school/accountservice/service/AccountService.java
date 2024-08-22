package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;

    @Transactional(readOnly = true)
    public AccountDto getAccount(long accountId) {
        Account account = findAccountById(accountId);
        return accountMapper.toDto(account);
    }

    @Transactional
    public void blockAccount(long accountId) {
        Account account = findAccountById(accountId);
        account.setAccountStatus(AccountStatus.FROZEN);
    }

    @Transactional
    public void unblockAccount(long accountId) {
        Account account = findAccountById(accountId);
        account.setAccountStatus(AccountStatus.OPEN);
    }

    @Transactional
    public void closeAccount(long accountId) {
        Account account = findAccountById(accountId);
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        accountValidator.validateAccountNumber(accountDto.getNumber());
        accountValidator.validateAccountOwner(accountDto.getOwnerType(), accountDto.getOwnerProjectId(),
                accountDto.getOwnerUserId());

        Account accountToSave = accountMapper.toEntity(accountDto);
        accountToSave.setAccountStatus(AccountStatus.OPEN);
        accountToSave.setVersion(1L);
        Account savedAccount = accountRepository.save(accountToSave);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    public AccountDto updateAccount(long accountId, UpdateAccountDto updateAccountDto) {
        Account account = findAccountById(accountId);

        accountValidator.validateAccountOwner(updateAccountDto.getOwnerType(), updateAccountDto.getOwnerProjectId(),
                updateAccountDto.getOwnerUserId());

        accountMapper.update(updateAccountDto, account);

        return accountMapper.toDto(accountRepository.save(account));
    }

    private Account findAccountById(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> {
            String errorMessage = "Couldn't find account with ID = " + accountId;
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }
}
