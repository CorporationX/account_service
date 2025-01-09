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
//    temporary leave options for speed tests
//    savingsAccountService.payToCustomers();
    savingsAccountService.payToClients();
  }

  @Scheduled(cron = "${cron.savings-payment}")
  public void payInterestRates() {
//    leave final one
    savingsAccountService.payToClients();
  }

}
