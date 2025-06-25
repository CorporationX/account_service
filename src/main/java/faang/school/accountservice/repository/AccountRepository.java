package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = """
            SELECT a
            FROM Account a
            WHERE a.number = :accountNumber
    """)
    Optional<Account> findByAccountNumber(String accountNumber);

    @Modifying
    @Query(value = """
            UPDATE Account a
            SET a.status = 'FROZEN'
            WHERE a.number = :accountNumber
    """)
    int blockAccountByNumber(String accountNumber);

    @Modifying
    @Query(value = """
            UPDATE Account a
            SET a.status = 'CLOSED', a.closedAt = CURRENT_TIMESTAMP
            WHERE a.number = :accountNumber
    """)
    int closeAccountByNumber(String accountNumber);

    @Modifying
    @Query(value = """
            DELETE FROM Account a
            WHERE a.number = :accountNumber
    """)
    int deleteAccountByNumber(String accountNumber);
}
