package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {
}
