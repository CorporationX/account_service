package faang.school.accountservice.repository;

import faang.school.accountservice.model.RateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateHistoryRepository extends JpaRepository<RateHistory, Long> {

}
