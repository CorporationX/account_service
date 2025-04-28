package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.CardType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountGeneratorScheduler {
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Value("${spring.task.scheduling.account_generator.cards_per_batch}")
    private int batchSize;

    @Scheduled(cron = "${spring.task.scheduling.account_generator.cron_expression}")
    public void generateAccountNumbers() {
        for (CardType cardType : CardType.values()) {
           freeAccountNumbersService.generateAccountNumbersForType(cardType, batchSize);
        }
    }
}
