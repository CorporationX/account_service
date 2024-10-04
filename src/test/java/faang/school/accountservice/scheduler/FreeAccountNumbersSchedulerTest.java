package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersSchedulerTest {
    private static final String SAVINGS_ACCOUNT_PREFIX = "8800";
    private static final long MAX_ACCOUNT_QUANTITY = 100L;
    private FreeAccountNumbersScheduler scheduler;
    @Mock
    private FreeAccountNumbersRepository repository;

    @BeforeEach
    void setup() {
        scheduler = new FreeAccountNumbersScheduler(SAVINGS_ACCOUNT_PREFIX,MAX_ACCOUNT_QUANTITY, repository);
    }

    @Test
    void createAndSaveFreeAccountNumbers() {
        // given - precondition
        long existedAccountsQuantity = 55L;

        when(repository.countByType(AccountType.SAVINGSACCOUNT)).thenReturn(existedAccountsQuantity);

        ArgumentCaptor<List<FreeAccountNumber>> captor = ArgumentCaptor.forClass(List.class);

        // when - action
        scheduler.createAndSaveFreeAccountNumbers();

        // then - verify the output
        verify(repository, times(1)).saveAll(captor.capture());

        List<FreeAccountNumber> savedAccounts = captor.getValue();
        assertThat(savedAccounts.size()).isEqualTo(MAX_ACCOUNT_QUANTITY - existedAccountsQuantity);

        savedAccounts
                .forEach(number -> assertThat((number.getNumber())).startsWith(SAVINGS_ACCOUNT_PREFIX));
    }
}