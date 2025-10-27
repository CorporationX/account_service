package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountSequenceRepository extends JpaRepository<AccountNumberSequence, AccountType> {

    @Modifying
    @Query("""
            UPDATE AccountNumberSequence a
            SET a.counter = a.counter + :batchSize,
            a.version = a.version + 1
            WHERE a.type = :type
            AND a.counter = :expectedCounter
            AND a.version = :expectedVersion
            """)
    int incrementCounter(
            @Param("type") AccountType type,
            @Param("expectedCounter") long expectedCounter,
            @Param("expectedVersion") long expectedVersion,
            @Param("batchSize") int batchSize
    );

    @Query("SELECT a FROM AccountNumberSequence a WHERE a.type = :type")
    Optional<AccountNumberSequence> findByType(@Param("type") AccountType type);
}
