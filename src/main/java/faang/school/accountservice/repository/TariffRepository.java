package faang.school.accountservice.repository;

import faang.school.accountservice.model.tariff.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
}