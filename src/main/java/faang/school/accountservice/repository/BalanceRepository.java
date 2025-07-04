package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BalanceRepository extends JpaRepository<Balance, UUID> {

    Optional<Balance> findByAccountId(UUID accountId);

    Optional<Balance> findByAccount_AccountNumber(String accountNumber);

    Boolean existsByAccountId(UUID accountId);
}