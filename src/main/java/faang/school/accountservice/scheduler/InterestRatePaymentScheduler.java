package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.savings.SavingsAccountService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterestRatePaymentScheduler {

  private final SavingsAccountService savingsAccountService;

  @PostConstruct
  public void init() {
    savingsAccountService.payToCustomers();
  }

  @Scheduled(cron = "${cron.savings-payment}")
  public void payInterestRates() {
    savingsAccountService.payToCustomers();
  }

}
