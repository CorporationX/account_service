package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountType> {

    @Query(nativeQuery = true, value = """
            INSERT INTO account_numbers_sequence (type, sequence_value)
            VALUES (:type, :sequence_value)
            """)
    AccountNumbersSequence createByType(AccountType type, @Param("sequence_value") Long sequenceValue);

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence
            SET sequence_value = sequence_value + 1
            WHERE type = :type
            RETURNING sequence_value;
            """)
    Long incrementAndGet(String type);
}
