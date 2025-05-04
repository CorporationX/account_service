package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

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
}
