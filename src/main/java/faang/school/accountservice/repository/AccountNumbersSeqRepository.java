package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSeqRepository extends JpaRepository<AccountSeq, AccountType> {
    @Query(nativeQuery = true, value = """
                    UPDATE account_numbers_sequence new 
                    SET counter = new.counter + :batchSize
                    FROM (
                        SELECT * FROM account_numbers_sequence 
                        WHERE type = :#{#type?.name()}
                        FOR UPDATE) old
                    WHERE new.type = :#{#type?.name()}
                    RETURNING old.counter
            """)
    long generateAccountNumbers(AccountType type, int batchSize);

    @Query(nativeQuery = true, value = """
                    INSERT INTO account_numbers_sequence (type, counter)
                    VALUES (:#{#type?.name()}, 1)
                    ON CONFLICT DO NOTHING
            """)
    @Modifying
    void createNumbersSequenceIfNecessary(AccountType type);
}
