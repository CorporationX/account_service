package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
