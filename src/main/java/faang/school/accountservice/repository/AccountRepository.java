package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = """
                    SELECT a
                    FROM Account a
                    WHERE a.number = :accountNumber
            """)
    Optional<Account> findByAccountNumber(String accountNumber);
}
