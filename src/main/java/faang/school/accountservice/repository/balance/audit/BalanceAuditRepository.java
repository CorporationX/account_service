package faang.school.accountservice.repository.balance.audit;

import faang.school.accountservice.model.balance.audit.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {
}
