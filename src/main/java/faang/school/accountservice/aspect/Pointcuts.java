package faang.school.accountservice.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
    @Pointcut("execution(* faang.school.accountservice.scheduler.InterestAccruer.accrueInterest())")
    public void accrueInterestSchedulerPointcut() {}

    @Pointcut("execution(* faang.school.accountservice.service.SavingsAccountService.*(..))")
    public void savingsAccountServicePointcut() {}

    @Pointcut("execution(* faang.school.accountservice.service.TariffService.*(..))")
    public void tariffServicePointcut() {}
}
