package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, AccountNumberType> {

    long countFreeAccountNumberById_Type(AccountNumberType accountNumberType);


    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_number
                    WHERE type = :type
                    AND digit_sequence in (
                          SELECT digit_sequence
                          FROM free_account_number
                          WHERE type = :type
                          ORDER BY digit_sequence
                          LIMIT 1
                    )
                    RETURNING digit_sequence
                    """)
    Optional<String> getAndRemoveFromPoolFreeAccountNumberByType(String type);
}
