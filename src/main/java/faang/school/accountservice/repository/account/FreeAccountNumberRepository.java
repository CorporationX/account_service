package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH selected AS (
            SELECT account_number FROM free_account_numbers WHERE type = :type LIMIT 1
            FOR UPDATE SKIP LOCKED
            ),
            deleted AS (
            DELETE FROM free_account_numbers WHERE id in (SELECT FROM selected)
            RETURNING *
            )
            SELECT * account_number, account_type FROM deleted;
            """)
    Optional<String> deleteAndReturnFreeAccountNumber(String type);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO free_account_number (account_type, account_number) VALUES (:accountType, :accountNumber)
            """)
    void saveFreeAccountNumber(String accountType, String accountNumber);

    long countByAccountType(String accountType);
}