package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.AccountNumberSequence;
import faang.school.accountservice.model.enums.AccountType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {
    @Query(nativeQuery = true, value = """
             INSERT INTO account_numbers_sequence (account_type)
             VALUES (:#{#accountType.toString()})
            """)
    @Modifying
    void createNewSequence(AccountType accountType);

    Optional<AccountNumberSequence> findByAccountType(AccountType accountType);

    @Transactional
    default AccountNumberSequence createAndGetSequence(AccountType accountType) {
        createNewSequence(accountType);
        return findByAccountType(accountType).orElseThrow(
                () -> new EntityNotFoundException("Sequence with account type %s not found".formatted(accountType)));
    }
}
