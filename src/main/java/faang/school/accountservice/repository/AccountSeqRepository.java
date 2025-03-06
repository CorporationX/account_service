package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.AccountSeq;
import faang.school.accountservice.model.account.enums.AccountType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountSeqRepository extends CrudRepository<AccountSeq, String> {
    @Query(nativeQuery = true, value = """
                        UPDATE account_number_sequence SET counter = counter + :batchSize
                        WHERE type = :type
            """)
    @Modifying
    void incrementCounter(String type, int batchSize);

    AccountSeq findByType(AccountType type);
}
