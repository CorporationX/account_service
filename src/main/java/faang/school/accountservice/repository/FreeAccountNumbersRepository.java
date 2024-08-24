package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Query(nativeQuery = true, value = """
            SELECT count(f) FROM FreeAccountNumber f WHERE f.id.type = ?1
            """)
    long countByAccountType(AccountType accountType);

    Optional<FreeAccountNumber> findByIdAccountType(AccountType accountType);

    void deleteById(FreeAccountNumberId freeAccountNumberId);

    @Transactional
    default Optional<FreeAccountNumber> getFirstAndDelete(AccountType accountType) {
        FreeAccountNumber freeAccountNumber = findByIdAccountType(accountType).orElse(null);

        if (freeAccountNumber == null) {
            return Optional.empty();
        } else {
            deleteById(freeAccountNumber.getId());
            return Optional.of(freeAccountNumber);
        }
    }
}
