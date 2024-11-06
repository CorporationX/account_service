package faang.school.accountservice.repository.account;

import java.util.Optional;

import faang.school.accountservice.enums.account.AccountEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM free_account_numbers
            WHERE type = :type
            LIMIT 1
        """
    )
    Optional<FreeAccountNumber> findFirstFreeAccountNumberByType(String type);

    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            DELETE FROM free_account_numbers
            WHERE type = :type AND account_number = :accountNumber
        """
    )
    int deleteFreeAccountNumberByTypeAndAccountNumber(String type, String accountNumber);

    @Query("SELECT COUNT(f) FROM FreeAccountNumber f WHERE f.id.type = :accountType")
    int countAllFreeAccountNumbersByType(@Param("accountType") AccountEnum accountType);
}
