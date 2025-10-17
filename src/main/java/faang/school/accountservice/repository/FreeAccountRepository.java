package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(value = """
            SELECT * FROM free_account_numbers
            WHERE account_type = :type
            ORDER BY account_number ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<FreeAccountNumber> findFirstByType(@Param("type") String type);
}
