package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.FreeAccountId;
import faang.school.accountservice.model.account.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true, value = """
    DELETE FROM free_account_numbers 
    WHERE type = :type 
    AND account_number = (
        SELECT account_number 
        FROM free_account_numbers 
        WHERE type = :type 
        LIMIT 1
    )
    RETURNING account_number, type
""")
    @Modifying
    FreeAccountNumber retrieveFirst(String type);

}
