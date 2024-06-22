package faang.school.accountservice.service.account_number;

import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountType;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

public interface AccountNumberService {
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    void getUniqueAccountNumber(Consumer<FreeAccountNumber> action, AccountType accountType);

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    FreeAccountNumber generateAccountNumber(AccountType accountType);
}
