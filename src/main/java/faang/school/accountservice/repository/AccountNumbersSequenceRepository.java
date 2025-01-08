package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
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
    default boolean incrementCounter(String accountType, Long expectedValue) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                AccountNumbersSequence sequence = findByAccountType(accountType);
                if (sequence != null && sequence.getCurrent().equals(expectedValue)) {
                    sequence.setCurrent(sequence.getCurrent() + 1);
                    save(sequence);
                    return true;
                }
                break;
            } catch (OptimisticLockException ex) {
                attempt++;
                System.err.println("Optimistic lock exception on attempt " + attempt + ": " + ex.getMessage());
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Failed to update after multiple retries due to concurrent updates.", ex);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }
}