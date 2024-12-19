package faang.school.accountservice.repository.account.freeaccounts;

import faang.school.accountservice.model.account.freeaccounts.FreeAccountId;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers fan 
                    WHERE fan.type = :type AND fan.account_number = (
                        SELECT account_number
                        FROM free_account_numbers
                        WHERE type = :type
                        LIMIT 1
                    )
                    RETURNING fan.account_number, fan.type
            """)
    @Modifying
    FreeAccountNumber retrieveFirst(String type);
}
