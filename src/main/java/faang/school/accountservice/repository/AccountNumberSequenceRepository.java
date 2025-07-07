package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {
    @Modifying
    @Query(value = """
    UPDATE account_number_sequence
    SET number = number + :value
    WHERE type = :type
    """, nativeQuery = true)
    void tryIncrement(@Param("type") String type, @Param("value") long value);

    @Query(value = """
    SELECT * FROM account_number_sequence
    WHERE type = :type
    """, nativeQuery = true)
    AccountNumberSequence getByType(@Param("type") String type);

    boolean existsByType(String type);
}
