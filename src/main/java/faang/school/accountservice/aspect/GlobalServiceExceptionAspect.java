package faang.school.accountservice.aspect;

import faang.school.accountservice.exception.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.ConcurrentModificationException;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class GlobalServiceExceptionAspect {

    @Pointcut("execution(* faang.school.accountservice.service.operation.OperationService.*(..)) || "
            + "execution(* faang.school.accountservice.service.balance.BalanceService.*(..))")
    public void serviceLayerMethods() {}

    @AfterThrowing(pointcut = "serviceLayerMethods()", throwing = "ex")
    private void logError(Exception ex) {
        Map<Class<? extends Exception>, String> exceptionLogMessages = Map.of(
                EntityNotFoundException.class, "EntityNotFoundException occurred: {}",
                DataAccessException.class, "DataAccessException occurred: {}",
                UnauthorizedAccessException.class, "UnauthorizedAccessException occurred: {}",
                OptimisticLockException.class, "OptimisticLockException occurred: {}",
                ConcurrentModificationException.class, "ConcurrentModificationException occurred: {}",
                RuntimeException.class, "RuntimeException occurred: {}"
        );

        String message = exceptionLogMessages.getOrDefault(ex.getClass(), "Exception occurred: {}");
        log.error(message, ex.getMessage(), ex);
    }
}
