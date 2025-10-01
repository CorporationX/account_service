package faang.school.accountservice.config.aspect;

import faang.school.accountservice.utils.CalculateExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(faang.school.accountservice.utils.CalculateExecutionTime)")
    public Object processCalculateExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CalculateExecutionTime annotation = method.getAnnotation(CalculateExecutionTime.class);
        String methodName = getMethodName(annotation, method);
        CalculateExecutionTime.TimeUnit timeUnit = annotation.timeUnit();
        Long startTime = System.nanoTime();

        try{
            return joinPoint.proceed();
        } finally {
            Long endTime = System.nanoTime();
            Long executionTime = endTime - startTime;
            Double convertedTime = convertTime(executionTime, timeUnit);
            String unitName = timeUnit.name();
            log.info("Execution time of " + methodName + " is " + convertedTime + " in " + unitName);
        }
    }

    private double convertTime(Long executionTime, CalculateExecutionTime.TimeUnit timeUnit) {
            switch (timeUnit) {
                case SECONDS: return executionTime / 1_000_000.0;
                case MINUTES: return executionTime / 1_000_000.0 * 60;
                case HOURS: return executionTime / 1_000_000.0 * 60 * 60;
                case DAYS: return executionTime / 1_000_000.0 * 24 * 60 * 60;
                case NANO_SECONDS: return executionTime;
                case MILLI_SECONDS:
                default: return executionTime / 1_000_000.0;
            }
    }

    private String getMethodName(CalculateExecutionTime annotation, Method method) {
        return annotation.name().isEmpty() ? method.getName() : annotation.name();
    }
}
