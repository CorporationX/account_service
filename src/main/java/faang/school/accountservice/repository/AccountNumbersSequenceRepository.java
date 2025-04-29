package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSequence;
import faang.school.accountservice.enums.AccountType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSequence, AccountType> {

    default void createCounter(AccountType type) {
        if (!existsById(type)) {
            AccountSequence sequence = new AccountSequence();
            sequence.setType(type);
            sequence.setCounter(0L);
            save(sequence);
        }
    }

    @Transactional
    @Modifying
    @Query("UPDATE AccountSequence s SET s.counter = s.counter + :increment, s.version = s.version + 1 " +
            "WHERE s.type = :type AND s.counter = :expectedCounter AND s.version = :version")
    int incrementCounterIfMatchInternal(@Param("type") AccountType type,
                                        @Param("increment") long increment,
                                        @Param("expectedCounter") long expectedCounter,
                                        @Param("version") long version);

    default boolean incrementCounterIfMatch(AccountType type,
                                            long increment,
                                            long expectedCounter,
                                            long version) {
        int updatedRows = incrementCounterIfMatchInternal(type, increment, expectedCounter, version);
        return updatedRows == 1;
    }
}
