package faang.school.accountservice.aspect.balance.audit;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.service.balance.audit.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class BalanceAuditSupervisorAspect {
    private BalanceAuditService balanceAuditServiceImpl;

    @Around("@annotation(BalanceAuditSupervisor)")
    public Object logBalanceAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        Object balanceObject = joinPoint.proceed();
        if (balanceObject instanceof BalanceDto balanceDto) {
            balanceAuditServiceImpl.addAuditFromNewBalance(balanceDto);
            return balanceDto;
        }
        return balanceObject;
    }
}
