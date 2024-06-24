package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value("${account.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${account.generate.cron}")
    public void createFreeAccountNumbers() {
        log.info("Generate account numbers");
        Arrays.stream(AccountType.values()).sequential()
                .forEach(accountType ->
                        freeAccountNumbersService.generateAccountNumbers(batchSize, accountType));
    }
}
