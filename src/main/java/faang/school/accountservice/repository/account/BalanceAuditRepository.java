package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {
}
