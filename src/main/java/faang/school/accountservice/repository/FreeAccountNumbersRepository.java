package faang.school.accountservice.repository;

import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers
                    WHERE type = :type and number = (
                        SELECT number
                        FROM free_account_numbers
                        WHERE type = :type
                        LIMIT 1
                    )
                    RETURNING id, number, type;
                    """)
    FreeAccountNumber getAndDeleteFirst(String type);
}
