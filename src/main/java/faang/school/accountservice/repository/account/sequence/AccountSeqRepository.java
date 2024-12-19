package faang.school.accountservice.repository.account.sequence;

import faang.school.accountservice.model.account.sequence.AccountSeq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSeqRepository extends JpaRepository<AccountSeq, String> {

    @Query(nativeQuery = true, value = """
            UPDATE account_number_sequence SET counter = counter + 1
            WHERE type = :type
            RETURNING type, counter
            """)
    @Modifying
    AccountSeq incrementCounter(String type);
}
