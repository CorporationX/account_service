package faang.school.accountservice.aspect;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class BalanceAuditAspect {

    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountRepository accountRepository;
    private final BalanceAuditService balanceAuditService;

    @AfterReturning(pointcut = "@annotation(AuditBalanceChange)", returning = "object")
    public void auditBalanceChange(Object object) throws Throwable {
        if (object instanceof Balance balance) {
            balanceAuditService.createBalanceAudit(balance);
        } else if (object instanceof Account account) {
            balanceAuditService.createBalanceAudit(account);
        }
    }
}
