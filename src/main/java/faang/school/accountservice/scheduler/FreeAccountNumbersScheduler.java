package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FreeAccountNumbersScheduler {

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "${account.numbers.free.cron}")
    public void createFreeAccountNumbers() {
        log.info("Creating batch free numbers");
        if (freeAccountNumberService.countByAccountType(AccountType.CURRENCY.toString()) < 1000) {
            freeAccountNumberService.generateAndSaveFreeAccountNumbers(AccountType.CURRENCY.toString(), 1000);
        }

        if (freeAccountNumberService.countByAccountType(AccountType.LEGAL.toString()) < 1000) {
            freeAccountNumberService.generateAndSaveFreeAccountNumbers(AccountType.LEGAL.toString(), 1000);
        }

        if (freeAccountNumberService.countByAccountType(AccountType.INDIVIDUAL.toString()) < 1000) {
            freeAccountNumberService.generateAndSaveFreeAccountNumbers(AccountType.INDIVIDUAL.toString(), 1000);
        }
    }
}
