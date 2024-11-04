package faang.school.accountservice.repository;

import faang.school.accountservice.model.number.FreeAccountNumber;
import faang.school.accountservice.enums.AccountNumberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, AccountNumberType> {

    long countFreeAccountNumberByType(AccountNumberType accountNumberType);

    @Query(value = """
        SELECT f.digitSequence
        FROM FreeAccountNumber f
        WHERE f.type = :type
        ORDER BY f.digitSequence
        LIMIT 1
        """)
    Optional<String> getFreeAccountNumberByType(AccountNumberType type);


    @Modifying
    @Query(value = """
            DELETE FROM FreeAccountNumber f
            WHERE f.digitSequence = :digitSequence
            """)
    int removeFreeAccountNumber(String digitSequence);
}
