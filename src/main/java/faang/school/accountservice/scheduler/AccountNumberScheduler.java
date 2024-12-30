package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${spring.account.number.butch.size}")
    private int butchSize;

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void generateDebit() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.DEBIT, butchSize);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void generateCredit() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CREDIT, butchSize);
    }
}
