package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountNumbersSequence;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, Long> {

    @Transactional
    default void createAccountTypeCounter(String accountType) {
        if (findByAccountType(accountType) == null) {
            AccountNumbersSequence sequence = new AccountNumbersSequence();
            sequence.setAccountType(accountType);
            sequence.setCurrent(0L);
            save(sequence);
        }
    }

    AccountNumbersSequence findByAccountType(String accountType);

    @Transactional
    @Retryable(
            value = OptimisticLockException.class,
            backoff = @Backoff(delay = 100)
    )
    default boolean incrementCounter(String accountType, Long expectedValue) {
        AccountNumbersSequence sequence = findByAccountType(accountType);
        if (sequence != null && sequence.getCurrent().equals(expectedValue)) {
            sequence.setCurrent(sequence.getCurrent() + 1);
            save(sequence);
            return true;
        }
        return false;
    }
}