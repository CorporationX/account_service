package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountSeqRepository extends JpaRepository<AccountNumbersSequence, String> {

    Optional<AccountNumbersSequence> findByType(String accountType);

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence SET counter = counter + :batchSize
            WHERE account_type = :type
            """)
    @Modifying
    void incrementCounter(String type, int batchSize);

}
