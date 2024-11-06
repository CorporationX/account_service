package faang.school.accountservice.service.generator;

import faang.school.accountservice.config.account.AccountProperties;
import faang.school.accountservice.enums.account.AccountEnum;
import faang.school.accountservice.service.account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersGeneratorSchedulerService {

    private final AccountProperties accountProperties;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional
    @Scheduled(cron = "${account.accountNumbers.scheduler.cron}")
    public void createRequiredFreeAccountNumbersPool() {
        for (int i = 0; i < AccountEnum.values().length; i++) {
            AccountEnum accountType = AccountEnum.values()[i];
            int freeAccountNeeds = accountProperties.getAccountNumbers()
                    .getNeedsOfFreeAccountNumbers().get(i);
            int existingAccountsNumberByType = freeAccountNumbersService
                    .countAllFreeAccountNumbersByType(accountType);
            if((freeAccountNeeds - existingAccountsNumberByType) > 0){
                freeAccountNumbersService.generateAccountNumbers(accountType,
                        freeAccountNeeds - existingAccountsNumberByType);
            }
        }
    }
}
