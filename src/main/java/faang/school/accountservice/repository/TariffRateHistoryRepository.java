package faang.school.accountservice.repository;

import faang.school.accountservice.entity.TariffRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TariffRateHistoryRepository extends JpaRepository<TariffRateHistory, Long> {
    @Query(value = """
            SELECT trh.* FROM tariff_history th
            JOIN tariff_rate_history trh ON th.tariff_id = trh.tariff_id
            WHERE th.account_id = :id
            ORDER BY th.created_at DESC, trh.created_at DESC
            LIMIT 1;
            """, nativeQuery = true)
    Optional<TariffRateHistory> findCurrentRateByAccountId(UUID id);

}
