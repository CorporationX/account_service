package faang.school.accountservice.repository;

import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM free_account_numbers fan
            WHERE fan.type = :type AND free_account_numbers = (
                SELECT account_number
                FROM free_account_numbers
                WHERE account_type = :type
                LIMIT 1)
            RETURNING fan.number
           """)
    FreeAccountNumber findAndRemoveFirstFreeAccountNumber(String type);
}
