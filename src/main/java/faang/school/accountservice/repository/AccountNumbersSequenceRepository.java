package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

    Optional<AccountNumberSequence> getByAccountType(String accountType);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = """
                    UPDATE account_numbers_sequence
                    SET current_counter = current_counter + 1
                    WHERE account_type =:accountType
                    AND current_counter = :expectedValue
                    RETURNING current_counter
                    """)
    Optional<Long> incrementCounter(@Param("accountType") String accountType,
                                    @Param("expectedValue") long expectedValue);
}
