package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {
    @Query(nativeQuery = true, value = """
            DELETE FROM free_account_numbers
            WHERE account_type = :accountType
            RETURNING *
            """)
    @Modifying
    Long getFreeAccount(AccountType accountType);
}
