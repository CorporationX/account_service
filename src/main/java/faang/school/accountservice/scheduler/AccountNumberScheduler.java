package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.service.free.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch_size}")
    private int batchSize;

    private final FreeAccountNumbersService freeAccountNumbersService;

    @Scheduled(cron = "${account.number.cron_to_create_new_numbers}")
    public void generateIndividualNumber(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.CHECKING_ACCOUNT_INDIVIDUAL, batchSize);
    }

    @Scheduled(cron = "${account.number.cron_to_create_new_numbers}")
    public void generateLegalEntityNumber(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.CHECKING_ACCOUNT_LEGAL_ENTITY, batchSize);
    }

    @Scheduled(cron = "${account.number.cron_to_create_new_numbers}")
    public void generateCurrencyNumber(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.CURRENCY_ACCOUNT, batchSize);
    }

    @Scheduled(cron = "${account.number.cron_to_create_new_numbers}")
    public void generateSavingNumber(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.SAVING_ACCOUNT, batchSize);
    }

    @Scheduled(cron = "${account.number.cron_to_create_new_numbers}")
    public void generateInvestmentNumber(){
        freeAccountNumbersService.generateAccountNumbers(AccountType.INVESTMENT_ACCOUNT, batchSize);
    }

}
