package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.AccountSeq;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountSeqRepository extends CrudRepository<AccountSeq, String> {
    @Query(nativeQuery = true, value = """
            UPDATE account_number_sequence SET counter = counter + :batchSize
            WHERE type = :type
            RETURNING type, counter, old.counter as initialValue
""")
    @Modifying
    AccountSeq incrementCounter(String type, int batchSize);

}
