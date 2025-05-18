package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccountId(Long accountId);
}