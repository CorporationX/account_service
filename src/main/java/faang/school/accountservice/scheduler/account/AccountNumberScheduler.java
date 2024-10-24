package faang.school.accountservice.scheduler.account;

import faang.school.accountservice.config.account.AccountNumberConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountNumberScheduler {

    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountNumberConfig accountNumberConfig;

    @Scheduled(cron = "${account.numbers.generation.cron}")
    public void generateRegularAccountNumbers() {
        freeAccountNumbersService.ensureMinimumNumbers(AccountType.CHECKING_INDIVIDUAL, accountNumberConfig.getCheckingIndividual());
    }
}
