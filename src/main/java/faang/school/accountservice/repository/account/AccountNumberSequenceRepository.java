package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO account_number_sequence (account_type, current_value) VALUES (:accountType, :value)
            """)
    void createSequence(String accountType, Long value);

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE account_number_sequence
            SET current_value = current_value + 1
            WHERE account_type = :accountType AND current_value = :currentValue
            """)
    void incrementSequence(String accountType, Long currentValue);

    @Query(nativeQuery = true, value = """
            SELECT current_value FROM account_number_sequence
            WHERE account_type = :accountType
            """)
    Optional<Long> getCurrentValue(String accountType);
}
