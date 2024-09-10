package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Query(nativeQuery = true, value = """
            SELECT count(f) FROM FreeAccountNumber f WHERE f.id.type = ?1
            """)
    long countByAccountType(AccountType accountType);

    @Query(nativeQuery = true, value = """
        SELECT f.account_type, f.number 
        FROM free_account_number f 
        WHERE f.account_type = :#{#accountType.toString()} 
        ORDER BY f.number ASC 
        LIMIT 1
        """)
    @Transactional(readOnly = true)
    Optional<FreeAccountNumber> findFirstByAccountType(@Param("accountType") AccountType accountType);

    void deleteById(FreeAccountNumberId freeAccountNumberId);


}
