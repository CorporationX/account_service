package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.number.FreeAccountNumbersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    private final AccountNumberProperties props;

    private final FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDebitAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CURRENT, props.getBatchSize());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCreditAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbers(AccountType.CREDIT, props.getBatchSize());
    }
}
