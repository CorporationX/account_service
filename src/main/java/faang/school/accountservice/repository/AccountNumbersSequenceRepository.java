package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.SequenceAlreadyExistsException;
import faang.school.accountservice.exception.SequenceLockException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountType> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM AccountNumbersSequence s WHERE s.accountType = :accountType")
    Optional<AccountNumbersSequence> findByAccountTypeWithLock(@Param("accountType") AccountType accountType);

    default AccountNumbersSequence createSequenceForType(AccountType accountType) {
        if (existsById(accountType)) {
            throw new SequenceAlreadyExistsException(accountType);
        }

        AccountNumbersSequence sequence = new AccountNumbersSequence(accountType);
        return save(sequence);
    }

    default AccountNumbersSequence getOrCreateSequence(AccountType accountType) {
        return findById(accountType).orElseGet(() -> createSequenceForType(accountType));
    }

    default Long incrementSequenceWithOptimisticLock(AccountType accountType) {
        AccountNumbersSequence sequence = getOrCreateSequence(accountType);
        Long newValue = sequence.incrementAndGet();
        save(sequence);
        return newValue;
    }

    default Long reserveSequenceBlock(AccountType accountType, int count, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                AccountNumbersSequence sequence = getOrCreateSequence(accountType);
                Long startValue = sequence.getCurrentSequence();

                sequence.setCurrentSequence(startValue + count);
                save(sequence);

                return startValue + 1;

            } catch (OptimisticLockException e) {
                try {
                    Thread.sleep(1 + attempt * 2);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SequenceLockException(
                            "Failed to reserve sequence block for " + accountType + " after " + maxRetries + " attempts", e
                    );
                }
            }
        }

        throw new AccountNumberGenerationException(
                String.format("Failed to reserve sequence block for %s after %d attempts",
                        accountType, maxRetries));
    }

    default Long getNextSequenceValue(AccountType accountType, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return incrementSequenceWithOptimisticLock(accountType);
            } catch (OptimisticLockException e) {
                if (attempt == maxRetries - 1) {
                    throw new SequenceLockException(
                            "Failed to reserve sequence block for " + accountType + " after " + maxRetries + " attempts", e
                    );
                }

                try {
                    Thread.sleep(1 + attempt * 2);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new AccountNumberGenerationException("Thread interrupted while waiting for retry", ie);
                }
            }
        }

        throw new AccountNumberGenerationException("Unexpected state in getNextSequenceValue");
    }
}