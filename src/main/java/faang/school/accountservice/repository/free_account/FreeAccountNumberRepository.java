package faang.school.accountservice.repository.free_account;

import faang.school.accountservice.entity.free_account.FreeAccountNumber;
import faang.school.accountservice.entity.free_account.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Query(value = """
                      DELETE FROM free_account_number 
                      WHERE (account_type, account_number) IN (
                            SELECT account_type, account_number
                            FROM free_account_number
                            WHERE account_type = :accountType
                            ORDER BY account_number
                            LIMIT 1
                      )
                      RETURNING account_type, account_number
                   """, nativeQuery = true
    )
    @Modifying
    Optional<FreeAccountNumber> deleteFirst(@Param("accountType") String accountType);
}
