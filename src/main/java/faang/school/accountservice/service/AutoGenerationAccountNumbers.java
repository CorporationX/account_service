package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoGenerationAccountNumbers {
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value("${account.number.batch.size}")
    private int batchSize;

    @Scheduled(cron = "${account.number.cron}")
    public void generationDebitAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbersUpToQuantity(AccountType.DEBIT, batchSize);

    }

    @Scheduled(cron = "${account.number.cron}")
    public void generationSavingsAccountNumbers() {
        freeAccountNumbersService.generateAccountNumbersUpToQuantity(AccountType.SAVINGS, batchSize);
    }
}
