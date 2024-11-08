package faang.school.accountservice.scheduler.account;

import faang.school.accountservice.config.account.AccountNumberConfig;
import faang.school.accountservice.dto.account.FreeAccountNumberDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountNumberSchedulerTest {

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @Mock
    private AccountNumberConfig accountNumberConfig;

    @InjectMocks
    private AccountNumberScheduler accountNumberScheduler;

    private int checkingIndividualBatchSize;
    private int savingsBatchSize;
    private int investmentBatchSize;

    @BeforeEach
    public void setUp() {
        checkingIndividualBatchSize = 1000;
        savingsBatchSize = 400;
        investmentBatchSize = 100;
    }

    @Test
    public void testGenerateCheckingIndividualAccountNumbers_Success() {
        when(accountNumberConfig.getBatchSize()).thenReturn(checkingIndividualBatchSize);

        FreeAccountNumberDto freeAccountNumberDto = FreeAccountNumberDto.builder()
                .type(AccountType.CHECKING_INDIVIDUAL)
                .batchSize(accountNumberConfig.getBatchSize())
                .build();

        accountNumberScheduler.generateCheckingIndividualAccountNumbers();

        verify(freeAccountNumbersService).createFreeAccountNumbers(freeAccountNumberDto);

    }

    @Test
    public void testGenerateSavingsAccountNumbers_Success() {
        when(accountNumberConfig.getBatchSize()).thenReturn(savingsBatchSize);

        FreeAccountNumberDto expectedDto = FreeAccountNumberDto.builder()
                .type(AccountType.SAVINGS_ACCOUNT)
                .batchSize(savingsBatchSize)
                .build();

        accountNumberScheduler.generateSavingsAccountNumbers();

        verify(freeAccountNumbersService).createFreeAccountNumbers(expectedDto);
    }

    @Test
    public void generateInvestmentAccountNumbers_Success() {
        when(accountNumberConfig.getBatchSize()).thenReturn(investmentBatchSize);

        FreeAccountNumberDto expectedDto = FreeAccountNumberDto.builder()
                .type(AccountType.INVESTMENT_ACCOUNT)
                .batchSize(investmentBatchSize)
                .build();

        accountNumberScheduler.generateInvestmentAccountNumbers();

        verify(freeAccountNumbersService).createFreeAccountNumbers(expectedDto);
    }
}