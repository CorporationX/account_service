package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {
    @Query("SELECT f.number FROM FreeAccountNumber f " +
            "WHERE f.type = :type " +
            "ORDER BY f.number ASC " +
            "LIMIT 1")
    String getSavingsAccountNumber(@Param("type") AccountType accountType);
    @Modifying
    @Transactional
    @Query("DELETE FROM FreeAccountNumber f " +
            "WHERE f.number = :number")
    void deleteSavingsAccountNumber(@Param("number") String accountNumber);
    long countByType(AccountType accountType);
    Optional<FreeAccountNumber> findByNumber(String accountNumber);
}