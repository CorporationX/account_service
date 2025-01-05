package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query(value = "INSERT INTO free_account_numbers (type, account_number) " +
            "VALUES (?1, ?2)"
            , nativeQuery = true)
    void saveNewFreeAccountNumber(String accountType, Long accountNumber);

    @Transactional
    @Query(value = "WITH deleted AS ( " +
            "    DELETE FROM free_account_numbers " +
            "    WHERE type = ?1 " +
            "    RETURNING account_number " +
            ") " +
            "SELECT account_number FROM deleted LIMIT 1", nativeQuery = true)
    Long getAndRemoveFirstFreeAccountNumber(String accountType);
}