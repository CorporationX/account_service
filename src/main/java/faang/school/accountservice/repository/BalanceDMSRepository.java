package faang.school.accountservice.repository;

import faang.school.accountservice.model.BalanceDMS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BalanceDMSRepository extends JpaRepository<BalanceDMS, UUID> {

    Optional<BalanceDMS> findByAccountId(UUID accountId);
}
