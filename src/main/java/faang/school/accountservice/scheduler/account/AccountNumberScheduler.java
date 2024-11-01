package faang.school.accountservice.scheduler.account;

import faang.school.accountservice.config.account.AccountNumberConfig;
import faang.school.accountservice.dto.account.FreeAccountNumberDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountNumberConfig accountNumberConfig;

    @Scheduled(cron = "${account.numbers.generation.cron-for-type-individual}")
    public void generateCheckingIndividualAccountNumbers() {
        FreeAccountNumberDto freeAccountNumberDto = FreeAccountNumberDto.builder()
                .type(AccountType.CHECKING_INDIVIDUAL)
                .batchSize(accountNumberConfig.getBatchSize())
                .build();

        freeAccountNumbersService.createFreeAccountNumbers(freeAccountNumberDto);
    }

    @Scheduled(cron = "${account.numbers.generation.cron-for-type-investment}")
    public void generateSavingsAccountNumbers() {
        FreeAccountNumberDto freeAccountNumberDto = FreeAccountNumberDto.builder()
                .type(AccountType.SAVINGS_ACCOUNT)
                .batchSize(accountNumberConfig.getBatchSize())
                .build();

        freeAccountNumbersService.createFreeAccountNumbers(freeAccountNumberDto);
    }

    @Scheduled(cron = "${account.numbers.generation.cron-for-type-investment}")
    public void generateInvestmentAccountNumbers() {
        FreeAccountNumberDto freeAccountNumberDto = FreeAccountNumberDto.builder()
                .type(AccountType.INVESTMENT_ACCOUNT)
                .batchSize(accountNumberConfig.getBatchSize())
                .build();

        freeAccountNumbersService.createFreeAccountNumbers(freeAccountNumberDto);
    }
}
