package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, Integer> {

    @Query(value = """
            SELECT * FROM free_account_numbers
            WHERE type = :type
            ORDER BY account_number ASC
            LIMIT 1;
            """, nativeQuery = true)
    FreeAccountNumber getNextAccountNumberByType(@Param("type") String accountNumberType);

    boolean existsByType(AccountNumberType type);
}
