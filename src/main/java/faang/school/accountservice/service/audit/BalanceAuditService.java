package faang.school.accountservice.service.audit;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.PendingOperation;

public interface BalanceAuditService {
    BalanceAudit saveAudit(Balance balance, PendingOperation operation);
    void deleteAudit(Long auditId);
}
