package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumbers, FreeAccountNumberId> {

    @Query(value = """
        DELETE FROM free_account_numbers 
        WHERE account_type = :accountType 
        AND (account_type, account_number) = (
            SELECT account_type, account_number 
            FROM free_account_numbers 
            WHERE account_type = :accountType 
            ORDER BY created_at ASC 
            LIMIT 1
        )
        RETURNING account_number
        """, nativeQuery = true)
    Optional<String> findAndDeleteFirstAvailableNumber(@Param("accountType") String accountType);

    @Query("SELECT COUNT(f) > 0 FROM FreeAccountNumbers f WHERE f.accountType = :accountType")
    boolean existsByAccountType(@Param("accountType") AccountType accountType);

    long countByAccountType(AccountType accountType);

    default FreeAccountNumbers createFreeAccountNumber(AccountType accountType, String accountNumber) {
        if (!accountType.isValidAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("Invalid account number: " + accountNumber);
        }

        FreeAccountNumbers freeNumber = new FreeAccountNumbers(accountType, accountNumber);
        return save(freeNumber);
    }
}