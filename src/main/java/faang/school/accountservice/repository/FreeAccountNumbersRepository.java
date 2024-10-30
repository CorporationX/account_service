package faang.school.accountservice.repository;

import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, String> {
    @Query(value = """
            DELETE FROM free_account_numbers
            WHERE account_type = :accountType
            AND account_number = (
                SELECT account_number FROM free_account_numbers
                WHERE account_type = :accountType
                ORDER BY account_number
                LIMIT 1
            )
            RETURNING account_number;
            """, nativeQuery = true)
    Optional<String> getFreeAccountNumber(String accountType);

    @Query(value = """
            select count(*) from free_account_numbers
            where account_type = :accountType
            """, nativeQuery = true)
    Integer getFreeAccountNumbersCountByType(String accountType);
}
