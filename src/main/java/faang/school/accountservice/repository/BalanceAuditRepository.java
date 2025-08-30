package faang.school.accountservice.repository;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.model.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {

    @Query("""
        select b from BalanceAudit b
        join AccountBalance a on a.id = b.accountId
        where b.accountId = :accountId and a.userId = :userId
    """)
    List<BalanceAudit> findByAccountIdAndUserId(Long accountId, Long userId);

    default List<BalanceAudit> getByAccountIdOrThrow(Long accountId, Long userId) {
        List<BalanceAudit> audits = findByAccountIdAndUserId(accountId, userId);
        if (audits.isEmpty()) {
            throw new EntityNotFoundException("No audits found for account " + accountId);
        }
        return audits;
    }
}