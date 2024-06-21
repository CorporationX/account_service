package faang.school.accountservice.service.account;

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

import static faang.school.accountservice.exception.message.AccountExceptionMessage.NON_EXISTING_ACCOUNT_EXCEPTION;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountVerifier accountVerifier;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountDto open(AccountDto accountDto) {
        accountVerifier.verifyOwnerExistence(accountDto.getOwnerUserId(), accountDto.getOwnerProjectId());

        Account createdAccount = accountRepository.save(accountMapper.toEntity(accountDto));
        return accountMapper.toDto(createdAccount);
    }

    public AccountDto getAccount(Long accountId) {
        Account account = getAccountModel(accountId);

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto changeStatus(Long accountId, AccountStatus status) {
        Account account = getAccountModel(accountId);
        accountVerifier.verifyStatusBeforeUpdate(account);

        if (status.equals(AccountStatus.CLOSED)) {
            account.setClosedAt(LocalDateTime.now());
        }
        account.setStatus(status);

        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional(readOnly = true)
    public Account getAccountModel(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    String message = String.format(NON_EXISTING_ACCOUNT_EXCEPTION.getMessage(), accountId);
                    return new NoSuchElementException(message);
                });
    }
}
