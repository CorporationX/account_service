package faang.school.accountservice.repository;

import faang.school.accountservice.model.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {

    List<BalanceAudit> findByAccountId(long id);
}
