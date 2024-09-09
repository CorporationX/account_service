package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {
    @Query(nativeQuery = true, value = """
                    DELETE FROM free_account_numbers fan
                    WHERE fan.type = :#{#type?.name()} AND fan.account_number = (
                        SELECT account_number FROM free_account_numbers
                        WHERE type = :#{#type?.name()}
                        LIMIT 1
                    )
                    RETURNING fan.type, fan.account_number
            """)
    FreeAccountNumber retrieveNumber(AccountType type);
}
