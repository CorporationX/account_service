package faang.school.accountservice.repository;

import faang.school.accountservice.model.account_number.FreeAccountId;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true,
            value = """
                    SELECT *
                    FROM free_account_numbers fan 
                    WHERE fan.type = :type AND currency = :currency AND fan.account_number = (
                    SELECT account_number
                    FROM free_account_numbers
                    WHERE type = :type AND currency = :currency
                      LIMIT 1
                    );
                          """)
    Optional<FreeAccountNumber> retrieveFirst(String type, String currency);
}
