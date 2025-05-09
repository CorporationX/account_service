package faang.school.accountservice.repository;

import faang.school.accountservice.model.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, UUID> {
}
