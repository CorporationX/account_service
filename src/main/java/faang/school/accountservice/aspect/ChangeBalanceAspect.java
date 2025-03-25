package faang.school.accountservice.aspect;

import faang.school.accountservice.annotation.AuditBalanceChange;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AuditEventType;
import faang.school.accountservice.service.balance.AsyncAuditService;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class ChangeBalanceAspect {
    private final AsyncAuditService auditService;
    private final BalanceService balanceService;

    @Around("@annotation(auditBalanceChange)")
    public void doAudit(ProceedingJoinPoint pjp, AuditBalanceChange auditBalanceChange) {
        Object[] args = pjp.getArgs();
        AuditEventType eventType = auditBalanceChange.eventType();

        UUID balanceId = (UUID) args[0];
        Balance previousBalance = balanceService.findById(balanceId);

        Object result = null;

        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            auditService.saveFailedBalanceAudit(eventType);
            log.error("Audit balance with id {} failed", balanceId);
        }

        Balance actualBalance = (Balance) result;
        if (actualBalance != null) {
            auditService.auditBalanceChange(previousBalance, actualBalance, eventType);
        }
    }
}
