package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.SavingsAccountRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountRateRepository extends JpaRepository<SavingsAccountRate, Long> {
}
