package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, Long> {

    AccountNumbersSequence findByAccountType(String accountType);

    @Modifying
    @Query(nativeQuery = true, value = """
            SELECT current_counter FROM account_numbers_sequence
            WHERE account_type = :type
            """)
    Optional<Long> findCurrentCounterByType(String type);

    @Transactional
    default AccountNumbersSequence createCounter(String accountType) {
        if (findByAccountType(accountType) != null) {
            return null;
        }
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(AccountType.valueOf(accountType));
        sequence.setCurrentCounter(0L);

        return save(sequence);
    }

    @Transactional
    default boolean incrementCounter(String accountType, long expectedCount) {
        AccountNumbersSequence sequence = findByAccountType(accountType);

        if(sequence != null && sequence.getCurrentCounter() == expectedCount) {
            sequence.setCurrentCounter(sequence.getCurrentCounter() + 1);
            save(sequence);
            return true;
        }

        return false;
    }

}
