package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import jakarta.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence
            SET counter = counter + :batchSize
            WHERE type = :type
            RETURNING type, counter, (counter - :batchSize) AS initialValue;
            """)
    AccountNumbersSequence incrementAndGetCounter(String type, int batchSize);
}

