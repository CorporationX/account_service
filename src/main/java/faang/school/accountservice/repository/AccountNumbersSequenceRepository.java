package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, AccountType> {

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence
            SET counter = counter + 1
            WHERE account_type = :typeName
            """)
    @Modifying
    void increment(String typeName);

    @Query(nativeQuery = true, value = """
            SELECT * FROM account_numbers_sequence
            WHERE account_type = :typeName
            RETURNING counter
            """)
    Long getCounterByType(String typeName);
}
