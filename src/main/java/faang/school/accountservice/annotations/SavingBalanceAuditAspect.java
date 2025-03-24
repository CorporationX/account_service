package faang.school.accountservice.annotations;

import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class SavingBalanceAuditAspect {
    @Autowired
    BalanceAuditRepository balanceAuditRepository;
    @Autowired
    BalanceAuditMapper balanceAuditMapper;

    @AfterReturning(pointcut = "@annotation(savingBalanceAudit)", returning = "result")
    public void methodToCall(JoinPoint joinPoint, SavingBalanceAudit savingBalanceAudit, Object result)
            throws Exception {
        String methodToCall = savingBalanceAudit.saveBalanceAudit();

        Object targetBean = joinPoint.getTarget();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Class<?> returnType = method.getReturnType();

        targetBean.getClass()
                .getMethod(methodToCall, returnType)
                .invoke(targetBean, result);
    }
}
