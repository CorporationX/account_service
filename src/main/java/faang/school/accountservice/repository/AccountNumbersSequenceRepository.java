package faang.school.accountservice.repository;

import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.AccountSequenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, AccountSequenceId> {

    @Query(nativeQuery = true, value = """
            WITH old_value AS (
                SELECT counter FROM account_numbers_sequence 
                WHERE type = :type AND currency = :currency
            )
            UPDATE account_numbers_sequence 
            SET counter = counter + :batchSize
            WHERE type = :type AND currency = :currency
            RETURNING type, currency, counter
            """)
    AccountNumberSequence incrementCounter(String type, String currency, int batchSize);
}
