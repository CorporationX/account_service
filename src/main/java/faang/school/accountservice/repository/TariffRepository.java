package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.TariffType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID> {
    Optional<Tariff> findByName(TariffType name);
}
