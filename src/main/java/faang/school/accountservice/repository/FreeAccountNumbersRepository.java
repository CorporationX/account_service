package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
    we use a native query here because we need to use FOR UPDATE SKIP LOCKED
    this lock ensures that we can't have multiple threads trying to create the same account number
 */
@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {
    @Query(value = """
        SELECT *
        FROM free_account_numbers
        WHERE type = :type
        ORDER BY account_number
        LIMIT 1
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    FreeAccountNumber findFirstForUpdate(String type);

    @Modifying
    @Query(value = """
        DELETE FROM free_account_numbers
        WHERE type = :type AND account_number = :accountNumber
        """, nativeQuery = true)
    int deleteByTypeAndAccountNumber(@Param("type") String type,
                                     @Param("accountNumber") String accountNumber);
}