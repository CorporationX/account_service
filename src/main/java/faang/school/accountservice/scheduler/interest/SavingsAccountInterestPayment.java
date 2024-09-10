package faang.school.accountservice.scheduler.interest;

import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SavingsAccountInterestPayment {

    private final SavingsAccountService savingsAccountService;

    @Scheduled(cron = "${task.interest-payment.savings-account.cron}")
    public void paySavingsAccountInterest() {
        savingsAccountService.payInterest();
    }
}
