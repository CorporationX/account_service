package faang.school.accountservice.service.balance;

public interface BalanceAuditService {
    void saveAudit(BalanceTest balance);
    void updateAudit(BalanceTest balance);
    void deleteAudit(Long auditId);
}
