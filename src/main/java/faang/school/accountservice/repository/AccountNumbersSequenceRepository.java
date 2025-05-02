package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSeq,String> {

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence 
            SET counter = counter + :batchSize
            WHERE type = :type
            RETURNING type, counter, (counter - :batchSize) AS initialValue
            """)
    AccountSeq incrementCounter(@Param("type") String type, @Param("batchSize") int batchSize);


    AccountSeq findByAccountType(AccountType type);

}
