package faang.school.accountservice.repository;

import faang.school.accountservice.entity.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {
}
