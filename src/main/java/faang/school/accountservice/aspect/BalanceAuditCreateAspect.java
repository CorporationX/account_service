package faang.school.accountservice.aspect;

import faang.school.accountservice.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceAuditCreateAspect {
    private final BalanceAuditAbstractAspect balanceAuditGetBalanceAspect;

    @Around("@annotation(faang.school.accountservice.annotation.BalanceAuditCreateAnnotation)")
    public Object createBalanceAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Account account)) {
            throw new IllegalStateException("Method annotated with @BalanceAuditCreateAnnotation must have an Account parameter");
        }
        Object result = joinPoint.proceed();

        UUID accountId = account.getId();

        balanceAuditGetBalanceAspect.saveBalanceAudit(accountId);

        return result;
    }
}
