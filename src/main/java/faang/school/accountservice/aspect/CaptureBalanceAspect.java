package faang.school.accountservice.aspect;

import faang.school.accountservice.service.balance.AsyncAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class CaptureBalanceAspect {
    private final AsyncAuditService auditService;

    @AfterReturning(pointcut = "@annotation(faang.school.accountservice.annotation.CaptureBalance)",
                    returning = "balance")
    public void doAudit() {
        auditService.processAudit();
    }
}
