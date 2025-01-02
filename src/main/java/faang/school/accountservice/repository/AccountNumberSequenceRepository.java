package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, String> {

    @Query(nativeQuery = true, value = """
            UPDATE account_number_sequence
            SET counter = counter + :batchSize
            WHERE type = :type
            RETURNING type, counter, old.counter AS initialValue
            """)
    @Modifying
    AccountNumberSequence incrementCounter(@Param("type") String type, @Param("batchSize") int batchSize);
}
