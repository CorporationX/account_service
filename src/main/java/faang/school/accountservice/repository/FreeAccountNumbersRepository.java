package faang.school.accountservice.repository;

import faang.school.accountservice.model.FreeAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccount, Long> {

    @Modifying
    @Query(value = "INSERT INTO free_account_numbers (type, account_number) " +
            "VALUES (?1, ?2)"
            , nativeQuery = true)
    void saveNewFreeAccountNumber(String accountType, Long accountNumber);

    @Transactional
    @Query(value = "DELETE FROM free_account_numbers " +
            "WHERE account_number = ( " +
            "    SELECT account_number FROM free_account_numbers " +
            "    WHERE type = ?1 " +
            "    LIMIT 1 " +
            ") " +
            "RETURNING account_number",
            nativeQuery = true)
    Long getAndRemoveFirstFreeAccountNumber(String accountType);
}