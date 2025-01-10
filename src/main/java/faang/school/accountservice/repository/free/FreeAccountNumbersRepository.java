package faang.school.accountservice.repository.free;

import faang.school.accountservice.entity.free.FreeAccountId;
import faang.school.accountservice.entity.free.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Modifying
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

    @Query(value = """
            DELETE FROM free_account_numbers fan
                    WHERE fan.type = :type AND fan.account_number = (
                        SELECT account_number
                        FROM free_account_numbers
                        WHERE type = :type
                        LIMIT 1
                    )
                    RETURNING fan.account_number
            """, nativeQuery = true)
    Optional<String> getFreeAccountNumber(String type);

    @Query(value = """
            SELECT COUNT(*)
            FROM free_account_numbers
            WHERE type = :type
            """, nativeQuery = true)
    long countByType(String type);
}
