package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccount_Id(Long accountId);
}
