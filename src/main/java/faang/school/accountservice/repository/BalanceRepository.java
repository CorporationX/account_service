package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
