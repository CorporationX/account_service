package faang.school.accountservice.repository;


import faang.school.accountservice.entity.TariffHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffHistoryRepository extends JpaRepository<TariffHistory, Long> {
}
