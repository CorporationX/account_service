package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM free_account_numbers 
        WHERE account_type = :accountType 
        AND account_number = (
            SELECT account_number 
            FROM free_account_numbers 
            WHERE account_type = :accountType 
            ORDER BY account_number 
            LIMIT 1
        ) 
        RETURNING account_number
        """, nativeQuery = true)
    Optional<String> findFirstAndDeleteByAccountType(@Param("accountType") String accountType);

    @Query("SELECT COUNT(f) FROM FreeAccountNumber f WHERE f.accountType = :accountType")
    long countByAccountType(@Param("accountType") AccountType accountType);

    @Modifying
    @Transactional
    @Query("DELETE FROM FreeAccountNumber f WHERE f.accountType = :accountType AND f.accountNumber = :accountNumber")
    int deleteByAccountTypeAndAccountNumber(@Param("accountType") AccountType accountType,
                                            @Param("accountNumber") String accountNumber);
}