package faang.school.accountservice.config.audit;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.balance.BalanceAudit;
import faang.school.accountservice.repository.balance.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Aspect
@Component
@RequiredArgsConstructor
public class BalanceAuditAspect {

    private final BalanceAuditRepository balanceAuditRepository;

    @After("execution(* faang.school.accountservice.repository.balance.BalanceRepository.*(..)) && args(balance)")
    public void afterBalanceSave(JoinPoint joinPoint, Balance balance) {
        BigDecimal authBalance = BigDecimal.valueOf(0);
        if (balance.getAuthorizationBalance() != null){
            authBalance = balance.getAuthorizationBalance();
        }

        BalanceAudit audit = BalanceAudit.builder()
                .account(balance.getAccount())
                .version(balance.getVersion())
                .paymentNumber(balance.getId())
                .authorizationBalance(authBalance)
                .actualBalance(balance.getActualBalance())
                .operationId(1)
                .build();

        balanceAuditRepository.save(audit);
    }

}
