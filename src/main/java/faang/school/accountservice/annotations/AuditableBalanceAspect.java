package faang.school.accountservice.annotations;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditableBalanceAspect {

    private final BalanceService balanceService;

    @AfterReturning(pointcut = "@annotation(auditableBalance)", returning = "result")
    public void afterCallSaveBalanceAudit(AuditableBalance auditableBalance, Object result) {

        if (result instanceof BalanceResponseDto) {
            BalanceResponseDto auditedBalance = (BalanceResponseDto) result;
            balanceService.saveBalanceAudit(auditedBalance);
        }
    }
}
