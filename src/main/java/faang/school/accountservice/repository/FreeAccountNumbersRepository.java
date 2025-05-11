package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM free_account_numbers
            WHERE type = :type
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """
    )
    Optional<FreeAccountNumber> findFirstByTypeForUpdate(@Param("type")String type);
}
