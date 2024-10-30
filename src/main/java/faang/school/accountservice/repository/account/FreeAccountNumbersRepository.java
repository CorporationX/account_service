package faang.school.accountservice.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountEnum;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM free_account_numbers
        WHERE id = (
            SELECT id
            FROM free_account_numbers
            WHERE account_type = :accountType
            LIMIT 1
        )
        RETURNING free_account_number
        """, 
        nativeQuery = true
    )
    Long findAndRemoveFreeAccountNumber(AccountEnum accountType);

    default FreeAccountNumber createFreeAccountNumber(AccountEnum accountType, Long freeAccountNumber) {
        FreeAccountNumber entity = new FreeAccountNumber();
        entity.setAccountType(accountType);
        entity.setFreeAccountNumber(freeAccountNumber);
        return save(entity);
    }
}
