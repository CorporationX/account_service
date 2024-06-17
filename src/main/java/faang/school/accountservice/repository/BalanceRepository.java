package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long>  {
}