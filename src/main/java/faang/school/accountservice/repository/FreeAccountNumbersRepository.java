package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.account_number.FreeAccountNumber;
import faang.school.accountservice.entity.account.account_number.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Modifying
    @Query(nativeQuery = true,
            value = """
            DELETE FROM free_account_numbers fan
                        WHERE fan.account_type = :accountType AND fan.account_number = (
                                    SELECT account_number
                                                FROM free_account_numbers
                                                WHERE account_type = :accountType
                                                LIMIT 1)
                        RETURNING fan.account_number, fan.account_type
            """)
    FreeAccountNumber retrieveFirst(AccountType accountType);
}
