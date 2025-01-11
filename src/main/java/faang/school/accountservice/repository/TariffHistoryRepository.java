package faang.school.accountservice.repository;

import faang.school.accountservice.model.RateHistory;
import faang.school.accountservice.model.TariffHistory;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffHistoryRepository extends JpaRepository<TariffHistory, Long> {
    @Query(value = "SELECT * FROM tariff_history where savings_account_id = :savings_account_id", nativeQuery = true)
    List<TariffHistory> findBySavingsAccountId(@Param("savings_account_id") Long savings_account_id);
}
