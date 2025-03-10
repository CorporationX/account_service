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
        ),
    inserted AS (
        INSERT INTO account_number_sequence (type, counter)
        SELECT :type, :batchSize
        WHERE NOT EXISTS (SELECT 1 FROM updated)
        RETURNING type, counter
        )
    SELECT type, counter
    FROM updated
    UNION ALL
    SELECT type, counter
    FROM inserted;
    """)
    AccountSeq incrementCounter(@Param("type") String type, @Param("batchSize") int batchSize);
}

