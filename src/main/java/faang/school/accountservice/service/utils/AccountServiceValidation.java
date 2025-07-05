package faang.school.accountservice.service.utils;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountServiceValidation {
    private final AccountRepository accountRepository;
    private final UserContext userContext;

    public void validateAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            log.error("Account with id {} does not exist", accountId);
            throw new IllegalArgumentException("Account with id " + accountId + " does not exist");
        }
    }

    public void validateCloseAccount(Long accountId, Account account) {
        hasPermission(account.getOwnerId());
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.warn("Account with id: {} is already closed", accountId);
            throw new DataValidationException("Account is already closed");
        }
        if (account.getBalance().getActualBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Account balance must be zero to close the account. Account ID: {}, Balance: {}",
                    accountId, account.getBalance().getActualBalance());
            throw new DataValidationException("Account balance must be zero to close the account.");
        }
    }

    public void hasPermission(Long ownerId) {
        Long currentUserId = userContext.getUserId();
        if (!Objects.equals(currentUserId, ownerId)) {
            log.warn("Permission denied for user ID: {}, required user ID: {}", currentUserId, ownerId);
            throw new SecurityException("Permission denied");
        }
    }
}
