package faang.school.accountservice.scheduler.account;

import faang.school.accountservice.config.account.AccountNumberConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
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

    @Test
    public void generateRegularAccountNumbers_ShouldCallEnsureMinimumNumbers() {
        int minNumbers = 1000;
        when(accountNumberConfig.getCheckingIndividual()).thenReturn(minNumbers);

        accountNumberScheduler.generateRegularAccountNumbers();

        verify(freeAccountNumbersService).ensureMinimumNumbers(AccountType.CHECKING_INDIVIDUAL, minNumbers);
    }
}