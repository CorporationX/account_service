package faang.school.accountservice.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.enums.account.AccountEnum;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountEnum> {

    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            UPDATE account_numbers_sequence
            SET counter = counter + :batchSize
            WHERE type = :type
            RETURNING type, counter, old.counter AS initialValue
        """
    )
    AccountNumbersSequence incrementCounter(String type, int batchSize);
    
}
