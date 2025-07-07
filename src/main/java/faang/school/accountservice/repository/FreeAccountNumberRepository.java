package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, Long> {
    @Query(value = """
        SELECT * FROM free_account_number
        WHERE type = :type
        ORDER BY id ASC
        LIMIT 1;
    """, nativeQuery = true)
    Optional<FreeAccountNumber> findFirstByType(@Param("type") String type);

    @Query(value = """
        SELECT COUNT(id) FROM free_account_number
        WHERE type = :type
    """, nativeQuery = true)
    int findAmountByType(@Param("type") String type);
}
