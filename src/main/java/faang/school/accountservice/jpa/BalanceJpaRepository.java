package faang.school.accountservice.jpa;

import faang.school.accountservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
}
