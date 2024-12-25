package faang.school.accountservice.repository;

import faang.school.accountservice.model.FreeAccountId;
import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers fan
                    WHERE fan.accounttype = :accounttype AND fan.account_number = (
                        SELECT account_number
                        FROM free_account_numbers
                        WHERE accounttype = :accounttype
                        LIMIT 1
                    )
                    RETURNING fan.account_number, fan.accounttype
                    """)
    @Modifying
    FreeAccountNumber retrieveFirst(String accounttype);
}
