package faang.school.accountservice.aspect;

import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.balance_audit.BalanceAuditService;
import org.springframework.stereotype.Component;

@Component
public class BalanceAuditAbstractAspect extends BalanceAuditAbstract {
    public BalanceAuditAbstractAspect(BalanceAuditService balanceAuditService,
                                      BalanceAuditRepository balanceAuditRepository) {
        super(balanceAuditService, balanceAuditRepository);
    }
}
