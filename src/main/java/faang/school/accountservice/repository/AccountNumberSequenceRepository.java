package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {
    @Modifying
    @Query(value = """
    UPDATE account_number_sequence
    SET number = CAST(CAST(number AS BIGINT) + :value AS VARCHAR)
    WHERE account_type = :type
    """, nativeQuery = true)
    void tryIncrement(@Param("type") AccountType type, @Param("value") int value);

    @Query(value = """
    INSERT INTO account_number_sequence (number, type)
    VALUES (:number, :type)
    """, nativeQuery = true)
    void createNewCounter(@Param("type") AccountType type, @Param("number") String initialNumber);

    @Query(value = """
    SELECT * FROM account_number_sequence
    WHERE type = :type
    """, nativeQuery = true)
    AccountNumberSequence getByType(@Param("type") AccountType type);

    boolean existsByType(AccountType type);
}
