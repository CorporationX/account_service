package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, AccountType> {

    @Query(nativeQuery = true, value = """
            DELETE FROM free_account_numbers fan
            WHERE fan.type = :accountType AND fan.account_number = (
            SELECT account_number FROM free_account_numbers
            WHERE type = :accountType
            LIMIT 1
            )
            RETURNING fan.account_number, fan.type
            """)
    FreeAccountNumber getAndDeleteAccountByType(String accountType);

    @Query(nativeQuery = true, value = """
            SELECT count(*)
            FROM free_account_numbers fan
            WHERE fan."type" = :accountType
            """)
    int getCurrentQuantityOfNumbersByType(String accountType);
}
