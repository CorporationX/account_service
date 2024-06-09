package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSequence, String> {

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence SET counter = counter + :batchSize
            WHERE type = :type
            RETURNING type, counter, old.counter AS initialValue
            """)
    @Modifying
    AccountSequence incrementCounter(String type, int batchSize);
}
