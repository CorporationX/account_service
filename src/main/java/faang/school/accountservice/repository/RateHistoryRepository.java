package faang.school.accountservice.repository;

import faang.school.accountservice.model.RateHistory;
import faang.school.accountservice.model.TariffHistory;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RateHistoryRepository extends JpaRepository<RateHistory, Long> {
    @Query(value = "SELECT * FROM rate_history where tariff_id = :tariffId", nativeQuery = true)
    RateHistory findByTariffId(@Param("tariffId") Long tariffId);
}
