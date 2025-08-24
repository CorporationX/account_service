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
                    DELETE FROM free_account_numbers fan
                    WHERE fan.type = :type AND fan.account_number = (
                        SELECT account_number
                        FROM free_account_numbers
                        WHERE type = :type
                        LIMIT 1
                    )
                    RETURNING fan.account_number, fan.type
            """)
    FreeAccountNumber retrieveFirst(String type);
}
