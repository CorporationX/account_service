package faang.school.accountservice.service;

import faang.school.accountservice.repository.SavingsAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static faang.school.accountservice.util.TestDataFactory.createSavingsAccount;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SavingsAccountUpdateAsyncServiceTest {
    @InjectMocks
    private SavingsAccountUpdateAsyncService service;
    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Test
    void updateInterestAsync() throws Exception {
       // given - precondition
        var savingsAccount = createSavingsAccount();

        // when - action
        var actualResult = CompletableFuture.runAsync(() -> service.updateInterestAsync(savingsAccount));

        // then - verify the output
        actualResult.get(5, SECONDS);
        verify(savingsAccountRepository, times(1)).save(savingsAccount);
        assertThat(savingsAccount.getLastInterestCalculationDate()).isNotNull();
    }
}