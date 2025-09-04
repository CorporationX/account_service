package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.account_number.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequences
            SET counter = counter + :batchSize
            WHERE account_type = :accountType
            RETURNING account_type, counter, old.counter AS initialValue
            """)
    AccountNumbersSequence incrementCounter(AccountType accountType, int batchSize);
}
