package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TariffRateCalculator {

    private final SavingsAccountService savingsAccountService;

    @Scheduled(cron = "${cron-setting.tariff-rate-calculator}")
    public void recalculateTariffRates() {
        log.debug("Start recalculating tariff rates on {}", LocalDateTime.now());
        savingsAccountService.recalculateTariffRates();
        log.debug("End recalculating tariff rates on {}", LocalDateTime.now());
    }
}
