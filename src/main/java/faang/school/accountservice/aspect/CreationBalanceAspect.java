package faang.school.accountservice.aspect;

import faang.school.accountservice.annotation.AuditBalanceCreation;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.service.balance.AsyncAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class CreationBalanceAspect {
    private final AsyncAuditService auditService;

    @AfterReturning(pointcut = "@annotation(auditBalanceCreation)",
                    returning = "balance")
    public void doAudit(Balance balance, AuditBalanceCreation auditBalanceCreation) {

        auditService.auditBalanceCreation(balance);
    }
}
