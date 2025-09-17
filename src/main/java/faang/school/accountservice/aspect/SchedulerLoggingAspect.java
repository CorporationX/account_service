package faang.school.accountservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SchedulerLoggingAspect {
    @Around("Pointcuts.accrueInterestSchedulerPointcut()")
    public void logAroundInterestScheduler(ProceedingJoinPoint proceeding) {
        log.info("Scheduled interest accrual started");
        try {
            proceeding.proceed();
            log.info("Scheduled interest accrual completed");
        } catch (Throwable e) {
            log.error("Scheduled interest accrual is failed. Cause: ", e);
        }
    }
}
