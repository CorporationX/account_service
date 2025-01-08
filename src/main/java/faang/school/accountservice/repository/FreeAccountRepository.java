package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true,
            value = """
                        DELETE FROM free_account_numbers fan
                        WHERE fan.type = :type AND account_number = (
                             SELECT account_number
                             FROM free_account_numbers
                             WHERE type = :type
                             LIMIT 1)
                             RETURNING fan.account_number,fan.type
                    """)
    FreeAccountNumber retrieveFirst(String type);

    @Query(nativeQuery = true,
            value = """
                        SELECT COUNT(*)
                        FROM free_account_numbers
                        WHERE type = :type
                    """)
    int allNumberTypeNow(String type);
}