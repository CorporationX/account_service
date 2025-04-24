package faang.school.accountservice.repository;

import faang.school.accountservice.entity.tariff.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    boolean existsByTypeName(String typeName);

    @Query("""
        SELECT tr.rate
        FROM Tariff t
        JOIN t.rates tr
        WHERE t.typeName = :typeName
        ORDER BY tr.changedAt DESC
        LIMIT 1
    """)
    Optional<BigDecimal> findLatestRateByTariffTypeName(@Param("typeName") String typeName);
}
