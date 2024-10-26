package faang.school.accountservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffHistoryRepository extends JpaRepository<TariffHistory, Long> {
}
