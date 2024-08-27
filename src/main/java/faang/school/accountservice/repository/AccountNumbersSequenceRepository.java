package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumberSequence;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {
    Optional<AccountNumberSequence> findByAccountType(AccountType accountType);

    @Query(nativeQuery = true, value = """
    INSERT INTO account_numbers_sequence (account_type)
    VALUES (:#{#accountType?.name()})
    """)
    @Modifying
    @Transactional
    void createNewAccountNumberSequence(@Param("accountType") AccountType accountType);

    @Transactional
    default AccountNumberSequence createSequenceIfNecessary(AccountType accountType) {
        createNewAccountNumberSequence(accountType);
        return findByAccountType(accountType).orElseThrow(()->
                new EntityNotFoundException("Couldn't find Account Number Sequence, type" + accountType.toString()));
    }

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence SET count = count + 1
            WHERE count = :count AND account_type = :#{#accountType.toString()}
            """)
    @Modifying
    int incrementCountByAccountType(long count, AccountType accountType);

    @Transactional
    default boolean isIncremented(long count, AccountType accountType) {
        return incrementCountByAccountType(count, accountType) > 0;
    }

}
