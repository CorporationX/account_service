package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSeqRepository extends JpaRepository<AccountSeq, String> {
    @Query(nativeQuery = true, value = """
    WITH updated AS (
        UPDATE account_number_sequence
        SET counter = counter + :batchSize
        WHERE type = :type
        RETURNING type, counter
        )
    SELECT type, counter, (counter - :batchSize) AS initialValue
    FROM updated
    UNION ALL
    SELECT :type AS type, :batchSize AS counter, 0 AS initialValue
    WHERE NOT EXISTS (SELECT 1 FROM updated);
    """)
    AccountSeq incrementCounter(@Param("type") String type, @Param("batchSize") int batchSize);
}

