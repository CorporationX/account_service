package faang.school.accountservice.repository;

import faang.school.accountservice.model.balance.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, UUID> {

    List<BalanceAudit> findByAccountNumber(String accountNumber);
}
