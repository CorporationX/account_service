package faang.school.accountservice.service.audit;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;

public interface BalanceAuditService {
    BalanceAudit saveAudit(Balance balance);
    void deleteAudit(Long auditId);
}
