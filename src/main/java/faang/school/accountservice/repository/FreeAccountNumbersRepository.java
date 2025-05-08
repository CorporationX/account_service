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
                        DELETE FROM free_account_numbers fan
                        WHERE fan.type = :type AND fan.number = (
                            SELECT number from free_account_numbers
                            WHERE type = :type
                            LIMIT 1
                        )
                        RETURNING fan.type, fan.number;
            
            """)
    Optional<FreeAccountNumber> getFreeAccount(String type);
}
