package faang.school.accountservice.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            DELETE FROM free_account_numbers fan
            WHERE fan.type = : type AND fan.accountNumber = (
                SELECT account_number
                FROM free_account_numbers
                WHERE type = :type
                LIMIT 1
            )
            RETURNING fan.type, fan.account_number
        """
    )
    FreeAccountNumber findAndRemoveFreeAccountNumber(String type);

}
