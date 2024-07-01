package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.service.account.FreeAccountNumberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class FreeAccountNumberScheduler {
    private final FreeAccountNumberService freeAccountNumberService;
    private final AccountType[] accountTypes;

    @Getter
    @Value("${schedulers.free_account_numbers.refilling.one_time_per_day.quantity}")
    private Integer quantityOneTimeInDayRefilling;

    @Getter
    @Value("${free_accounts.numbers.min_quantity}")
    private Integer minAccountsNumbersQuantity;


    @Async("getThreadPool")
    @Scheduled(cron = "${schedulers.free_account_numbers.refilling.one_time_per_day.cron}")
    public void generateNewAccounts() {
        for (AccountType type : accountTypes) {
            for (int i = 0; i < quantityOneTimeInDayRefilling; i++) {
                freeAccountNumberService.generateFreeAccount(type);
            }
        }
    }

    @Async("getThreadPool")
    @Scheduled(cron = "${schedulers.free_account_numbers.refilling.one_time_in_three_hours.cron}")
    public void generateMissingAccounts() {
        for (AccountType type : accountTypes) {
            long currentQuantity = freeAccountNumberService.countFreeAccountNumbersByType(type).longValue();
                while (currentQuantity <= minAccountsNumbersQuantity) {
                    freeAccountNumberService.generateFreeAccount(type);
                    currentQuantity++;
                }
        }
    }

    @Async("getThreadPool")
    @Scheduled(cron = "${schedulers.free_account_numbers.refilling.one_time_in_three_hours.cron}")
    public void generateNewSavingsAccountNumbers(){
        Arrays.stream(accountTypes)
                .forEach(type -> IntStream.range(0, quantityOneTimeInDayRefilling)
                        .forEach(i -> freeAccountNumberService.generateFreeAccount(type)));
    }
}