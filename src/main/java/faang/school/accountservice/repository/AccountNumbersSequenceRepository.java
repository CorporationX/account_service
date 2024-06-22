package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSeq, AccountType>{
    @Query(nativeQuery = true, value = """
                 UPDATE account_number_sequence SET counter = counter + :bucketSize
                 WHERE type = :typeString
            """)
    @Modifying
    void incrementCounter(String typeString, long bucketSize);

    @Query(nativeQuery = true, value = """
    SELECT a.type, a.counter, (a.counter - :bucketSize) AS initialValue
    FROM account_number_sequence a
    WHERE a.type = :typeString
        """)
    AccountSeq getCounterValues(@Param(value = "typeString")String typeString, @Param(value = "bucketSize")long bucketSize);

    @Query(nativeQuery = true, value = "SELECT a.* FROM account_number_sequence a WHERE a.type = :typeString")
    AccountSeq findByType(String typeString);
}
