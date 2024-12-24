package faang.school.accountservice.repository;

import faang.school.accountservice.model.interest_rate.InterestRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, Long> {
}