package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {
    Optional<AccountNumbersSequence> findByAccountType(String accountType);

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence
            SET current_number = current_number + :batchSize
            WHERE account_type =:accountType
            RETURNING account_type, current_number
            """)
    AccountNumbersSequence incrementCounter(String accountType, int batchSize);
}
