package faang.school.accountservice.repository.tariff;

import faang.school.accountservice.entity.tariff.TariffRateChangelog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRateChangelogRepository extends JpaRepository<TariffRateChangelog, Long> {
}
