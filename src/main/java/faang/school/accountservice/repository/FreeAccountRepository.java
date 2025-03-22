package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.FreeAccountId;
import faang.school.accountservice.model.account.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true,
            value = """
                    SELECT account_number, type
                    FROM free_account_numbers
                    WHERE type = :accountType
                    LIMIT 1
                    """)
    FreeAccountNumber findFirst(String accountType);

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers
                    WHERE type = :accountType AND account_number = :accountNumber
                    """)
    void deleteByAccountTypeAndAccountNumber(String accountType, long accountNumber);
}
