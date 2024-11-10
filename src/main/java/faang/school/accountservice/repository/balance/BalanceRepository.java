package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {
    Optional<Balance> findBalanceByAccountId(UUID accountId);
}
