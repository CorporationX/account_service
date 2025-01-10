package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.service.free.FreeAccountNumbersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountNumberSchedulerTest {

    @Value("${account.number.batch_size}")
    private int batchSize;

    @InjectMocks
    private AccountNumberScheduler accountNumberScheduler;

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @Test
    void testGenerateIndividualNumber(){
        accountNumberScheduler.generateIndividualNumber();
        verify(freeAccountNumbersService, times(1))
                .generateAccountNumbers(AccountType.CHECKING_ACCOUNT_INDIVIDUAL, batchSize);
    }

    @Test
    void testGenerateLegalEntityNumber(){
        accountNumberScheduler.generateLegalEntityNumber();
        verify(freeAccountNumbersService, times(1))
                .generateAccountNumbers(AccountType.CHECKING_ACCOUNT_LEGAL_ENTITY, batchSize);
    }

    @Test
    void testGenerateCurrencyNumber(){
        accountNumberScheduler.generateCurrencyNumber();
        verify(freeAccountNumbersService, times(1))
                .generateAccountNumbers(AccountType.CURRENCY_ACCOUNT, batchSize);
    }

    @Test
    void testGenerateSavingNumber(){
        accountNumberScheduler.generateSavingNumber();
        verify(freeAccountNumbersService, times(1))
                .generateAccountNumbers(AccountType.SAVING_ACCOUNT, batchSize);
    }

    @Test
    void testGenerateInvestmentNumber(){
        accountNumberScheduler.generateInvestmentNumber();
        verify(freeAccountNumbersService, times(1))
                .generateAccountNumbers(AccountType.INVESTMENT_ACCOUNT, batchSize);
    }




}