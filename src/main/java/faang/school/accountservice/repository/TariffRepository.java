package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Optional<Tariff> findTariffByTariffName(String tariffName);
}
