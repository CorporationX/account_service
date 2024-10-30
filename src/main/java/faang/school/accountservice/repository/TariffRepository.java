package faang.school.accountservice.repository;

import faang.school.accountservice.model.savings.Tariff;
import faang.school.accountservice.model.savings.TariffType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID> {
}
