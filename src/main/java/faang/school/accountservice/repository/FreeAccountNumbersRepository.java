package faang.school.accountservice.repository;

import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.model.account_number.FreeAccountNumberId;
import faang.school.accountservice.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Query(nativeQuery = true, value = """
            DELETE FROM free_account_number
            WHERE number = (
                SELECT number
                FROM free_account_number
                WHERE account_type = ?
                LIMIT 1
            )
            RETURNING *;
            """)
    @Modifying
    Optional<FreeAccountNumber> getAndDeleteFirst(AccountType accountType);
}
