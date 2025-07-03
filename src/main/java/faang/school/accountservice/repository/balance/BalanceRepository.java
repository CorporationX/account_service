package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {
    @Query(nativeQuery = true, value = """
            SELECT *
            FROM balance
            WHERE id = :id
            FOR UPDATE
            """)
    Optional<Balance> findByIdForUpdate(@Param("id") UUID balanceId);
}
