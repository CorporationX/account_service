package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.enums.AccountType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, String> {

    @Transactional
    @Modifying
    @Query("""
            UPDATE AccountNumberSequence ans
            SET ans.counter = ans.counter + :batchSize
            WHERE ans.type = :type AND ans.counter = :expectedCounter
            """)
    int incrementCounterIfMatch(@Param("type") AccountType type, @Param("batchSize") int batchSize, @Param("expectedCounter") Long expectedCounter);

    Optional<AccountNumberSequence> findByType(AccountType type);
}
