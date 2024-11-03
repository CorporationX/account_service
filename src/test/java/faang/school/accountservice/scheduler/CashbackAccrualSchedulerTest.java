package faang.school.accountservice.scheduler;

import faang.school.accountservice.service.CashbackTariffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CashbackAccrualSchedulerTest {

    @Mock
    private CashbackTariffService cashbackTariffService;

    @InjectMocks
    private CashbackAccrualScheduler cashbackAccrualScheduler;

    @Test
    void cashbackAccrual_dispatchesTasksSuccessfully() {
        int expectedDispatchedAccounts = 5;
        when(cashbackTariffService.earnCashbackOnExpensesAllAccounts()).thenReturn(expectedDispatchedAccounts);

        cashbackAccrualScheduler.cashbackAccrual();

        verify(cashbackTariffService).earnCashbackOnExpensesAllAccounts();
    }
}