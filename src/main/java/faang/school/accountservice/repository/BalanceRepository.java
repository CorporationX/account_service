package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BalanceRepository extends JpaRepository<Balance, UUID> {
    Optional<Balance> findByAccountId(Long id);

    default Balance findByAccountIdOrThrow(Long id) {
        return findByAccountId(id).orElseThrow(() -> new EntityNotFoundException("Not found balance with id " + id));
    }
}