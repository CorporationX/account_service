package faang.school.accountservice.repository;

import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Evgenii Malkov
 */
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    boolean existsTariffByType(TariffType type);

    Tariff findTariffByType(TariffType type);
}
