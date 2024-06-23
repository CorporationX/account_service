package faang.school.accountservice.repository.jpa;

import faang.school.accountservice.entity.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceJpaRepository extends JpaRepository<AccountNumbersSequence, Long> {
    @Query(nativeQuery = true, value = """
                    SELECT * FROM account_numbers_sequence
                    WHERE type = :type
                    LIMIT 1
            """)
    Optional<AccountNumbersSequence> findByType(String type);
}
