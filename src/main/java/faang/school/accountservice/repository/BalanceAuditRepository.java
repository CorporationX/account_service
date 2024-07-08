package faang.school.accountservice.repository;

import faang.school.accountservice.model.BalanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {

    List<BalanceAudit> findAllByAccountId(Long id);

    @Query("SELECT COALESCE(MAX(b.version), 0) FROM BalanceAudit b WHERE b.account.id = :id")
    long getLastVersionByAccountId(long id);

    List<BalanceAudit> findByPaymentNumber(Long paymentNumber);
}
