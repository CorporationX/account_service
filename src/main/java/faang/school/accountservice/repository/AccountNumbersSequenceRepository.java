package faang.school.accountservice.repository;

import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO account_numbers_sequence (account_type)
            VALUES (?)
            RETURNING *
            """)
    @Modifying
    AccountNumberSequence createSequence(AccountType accountType);

    @Query(nativeQuery = true, value = """
            WITH updated AS (
                UPDATE account_numbers_sequence SET count = count + 1
                WHERE count = ? AND account_type = ?
                RETURNING count
            )
            SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM updated
            """)
    @Modifying
    boolean incrementIfNumberEquals(int count, AccountType accountType);

    Optional<AccountNumberSequence> findByAccountType(AccountType accountType);
}
