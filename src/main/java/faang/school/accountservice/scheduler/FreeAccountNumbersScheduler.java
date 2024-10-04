package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumbersScheduler {
    @Value("${app.account.prefixes.savings_account}")
    private String savingsAccountPrefix;
    @Value("${app.account.max_quantity}")
    private long maxAccountQuantity;

    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Scheduled(cron = "${app.scheduling.free_account_number.cron}")
    public void createAndSaveFreeAccountNumbers(){
        var existedAccountsQuantity = freeAccountNumbersRepository.countByType(AccountType.SAVINGSACCOUNT);
        var requiredAccountQuantity = maxAccountQuantity  - existedAccountsQuantity;
        saveFreeAccountNumbers(requiredAccountQuantity);
    }

    private void saveFreeAccountNumbers(long accountQuantity){
        var freeAccountNumbers = LongStream.range(0, accountQuantity)
                .mapToObj(i -> generateNextNumber())
                .map(this::createFreeAccountNumber)
                .toList();
        freeAccountNumbersRepository.saveAll(freeAccountNumbers);
    }

    private FreeAccountNumber createFreeAccountNumber(String accountNumber){
        return FreeAccountNumber.builder()
                .type(AccountType.SAVINGSACCOUNT)
                .number(accountNumber)
                .build();
    }

    private String generateNextNumber() {
        AtomicLong counter = new AtomicLong();
        long nextNumber = counter.getAndIncrement();
        return String.format("%s%012d", savingsAccountPrefix, nextNumber);
    }
}