package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSeqRepository extends JpaRepository<AccountSequence, String> {

    AccountSequence findByType(String accountType);

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence SET counter = counter + :batchSize
            WHERE account_type = :type
            """)
    @Modifying
    void incrementCounter(String type, int batchSize);

}
