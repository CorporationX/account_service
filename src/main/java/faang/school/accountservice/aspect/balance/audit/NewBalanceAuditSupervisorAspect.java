package faang.school.accountservice.aspect.balance.audit;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.service.balance.audit.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class NewBalanceAuditSupervisorAspect {
    private final BalanceAuditService balanceAuditServiceImpl;

    @Around("@annotation(NewBalanceAuditSupervisor)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        Object object = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();

        BalanceDto balanceDto = (BalanceDto) object;
        for (Object arg : args) {
            if (arg instanceof PaymentDto) {
                PaymentDto paymentDto = (PaymentDto) args[1];
                balanceAuditServiceImpl.addAuditFromExistingBalance(balanceDto, paymentDto);
            }
        }
        return object;
    }
}
