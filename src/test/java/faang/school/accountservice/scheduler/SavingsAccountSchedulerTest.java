package faang.school.accountservice.scheduler;

import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.SavingsAccountUpdateAsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.util.TestDataFactory.createSavingsAccount;
import static java.util.List.of;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsAccountSchedulerTest {
    @InjectMocks
    private SavingsAccountScheduler savingsAccountScheduler;
    @Mock
    private SavingsAccountRepository savingsAccountRepository;
    @Mock
    private SavingsAccountUpdateAsyncService savingsAccountAsyncService;

    @Test
    void calculateAndUpdateInterest() {
        // given - precondition
        var accounts = of(createSavingsAccount());

        when(savingsAccountRepository.findAll()).thenReturn(accounts);

        // when - action
        savingsAccountScheduler.calculateAndUpdateInterest();

        // then - verify the output
        verify(savingsAccountAsyncService, times(1)).updateInterestAsync(accounts.get(0));
        verifyNoMoreInteractions(savingsAccountAsyncService);
    }
}