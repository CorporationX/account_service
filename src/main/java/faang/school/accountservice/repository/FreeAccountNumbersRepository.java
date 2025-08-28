package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true,
            value = """
                    WITH picked AS (
                        SELECT type, account_number
                        FROM free_account_numbers
                        WHERE type = :type
                        ORDER BY account_number
                        FOR UPDATE SKIP LOCKED
                        LIMIT 1
                    )
                    DELETE FROM free_account_numbers f
                    USING picked
                    WHERE f.type = picked.type
                      AND f.account_number = picked.account_number
                    RETURNING f.type, f.account_number
                    """)
    FreeAccountNumber retrieveFirst(String type);
}
