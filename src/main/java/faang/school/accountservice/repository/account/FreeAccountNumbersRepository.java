package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> ,
        FreeAccountNumbersCustomRepository {

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
    void deleteFreeAccountNumberByTypeAndAccountNumber(String type, String accountNumber);

    int countById_Type(AccountEnum type);
}
