package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BalanceRepository extends CrudRepository<Balance, Long> {
    Optional<Balance> findByAccountId(Long accountId);
}
