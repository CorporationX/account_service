package faang.school.accountservice.service.audit;

import faang.school.accountservice.model.balance.Balance;

public interface BalanceAuditService {
    void saveAudit(Balance balance);
    void updateAudit(Balance balance);
    void deleteAudit(Long auditId);
}
