package faang.school.accountservice.repository;

import faang.school.accountservice.model.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {

    List<BalanceAudit> findAllByAccountId(Long id);

    @Query(nativeQuery = true, value = """
            SELECT * FROM balance_audit WHERE account_id = ?1 AND created_at >= ?2 AND created_at <= ?3
            """)
    @Modifying
    List<BalanceAudit> findAllAuditsByInterval(Long accountId, LocalDateTime from, LocalDateTime to);
}
