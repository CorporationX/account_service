package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Query(nativeQuery = true, value = """
            DELETE FROM free_account_number
            WHERE account_number = (
                SELECT account_number FROM free_account_number
                WHERE type = :type
                LIMIT 1
                FOR UPDATE SKIP LOCKED)
            RETURNING account_number
            """)
    Optional<String> getFreeAccountNumberByType(String type);
}
