package faang.school.accountservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SavingsAccountLoggingAspect {
    @Before("Pointcuts.savingsAccountServicePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            log.info("{} called with args {}", methodName, args);
        } else {
            log.info("{} called", methodName);
        }
    }

    @AfterReturning(value = "Pointcuts.savingsAccountServicePointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();

        if (result != null) {
            log.info("{} completed with result {}", methodName, result);
        } else {
            log.info("{} completed", methodName);
        }
    }
}
