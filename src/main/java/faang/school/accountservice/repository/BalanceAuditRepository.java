package faang.school.accountservice.repository;

import faang.school.accountservice.entity.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {
    List<BalanceAudit> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}
