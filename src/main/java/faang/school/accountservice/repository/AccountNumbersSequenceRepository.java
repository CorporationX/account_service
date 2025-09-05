package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSequence, String> {

    @Query(nativeQuery = true,
            value = """
                    UPDATE account_number_sequence SET counter = counter + :batchSize
                    WHERE type = :type
                    RETURNING type, counter, counter - :batchSize AS initialValue;
                    """)
    AccountSequence incrementCounter(String type, int batchSize);
}
