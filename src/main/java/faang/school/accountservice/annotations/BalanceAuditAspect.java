package faang.school.accountservice.annotations;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.service.balance.BalanceServiceImpl;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BalanceAuditAspect {

    @Autowired
    private BalanceServiceImpl balanceService;

    @AfterReturning(pointcut = "@annotation(balanceAudit)", returning = "result")
    public void afterCallSaveBalanceAudit(BalanceAudit balanceAudit, Object result) {

        if (result instanceof BalanceResponseDto) {
            BalanceResponseDto auditedBalance = (BalanceResponseDto) result;
            balanceService.saveBalanceAudit(auditedBalance);
        }
    }
}
