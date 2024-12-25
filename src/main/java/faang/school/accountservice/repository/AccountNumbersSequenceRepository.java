package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountSeq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSeq, String> {

    @Query(nativeQuery = true, value = """
            UPDATE account_number_sequence SET counter = counter + :batchSize
            WHERE accounttype = :acccounttype
            RETURNING accounttype, counter, old.counter AS initialValue
            """)

    @Modifying
    AccountSeq incrementCounter(String acccounttype, int batchSize);

}
