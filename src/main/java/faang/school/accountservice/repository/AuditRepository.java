package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<BalanceAudit, Long> {

}
