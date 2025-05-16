package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository2 extends JpaRepository<Balance2, Long> {
    Optional<Balance2> findByAccountId(Long accountId);
}