
package faang.school.accountservice.repository.account.freeaccounts;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Query(value = """
    DELETE FROM free_account_numbers
    WHERE id IN (
        SELECT id FROM free_account_numbers
        WHERE type = :type
        ORDER BY id ASC
        LIMIT 1
    )
    RETURNING id, type, account_number
    """, nativeQuery = true)
    Optional<FreeAccountNumber> retrieveAndDeleteFirst(@Param("type") String type);
}

