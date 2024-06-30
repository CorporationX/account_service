package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSequence, Long> {

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence
            SET counter = counter + :batchSize
            WHERE type = :typeName
            """)
    @Modifying
    void incrementCounter(String typeName, int batchSize);

    @Query(nativeQuery = true, value = """
                    SELECT * FROM account_numbers_sequence WHERE type = :typeName
            """)
    AccountSequence getByType(String typeName);
}
