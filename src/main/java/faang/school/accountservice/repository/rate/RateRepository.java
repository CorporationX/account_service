package faang.school.accountservice.repository.rate;

import faang.school.accountservice.entity.rate.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByInterestRate(Double interestRate);
}
