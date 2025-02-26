package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO account_number_sequence (account_type, account_number) VALUES (:accountType, 0)
            """)
    void createSequence(String accountType);

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE account_number_sequence SET current_value = current_value + 1
            WHERE account_type = :accountType AND current_value = :currentValue RETURNING current_value
            """)
    Optional<Long> incrementSequence(String accountType, Long currentValue);
}
