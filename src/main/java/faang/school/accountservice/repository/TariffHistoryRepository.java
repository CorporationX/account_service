package faang.school.accountservice.repository;

import faang.school.accountservice.model.savings.TariffHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TariffHistoryRepository extends JpaRepository<TariffHistory, UUID> {
}
