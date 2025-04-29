package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers
                    WHERE (type, account_number) IN (
                        SELECT type, account_number
                        FROM free_account_numbers
                        WHERE type = :type
                        ORDER BY account_number
                        LIMIT 1
                    )
                    RETURNING account_number, type
                    """)
    List<Object[]> retrieveFirst(String type);

    @Transactional
    default FreeAccountNumber createAccountNumber(AccountType type, long accountNumber) {
        FreeAccountId id = new FreeAccountId(type, accountNumber);
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(id);
        return save(freeAccountNumber);
    }
}
