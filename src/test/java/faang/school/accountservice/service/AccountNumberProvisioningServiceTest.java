package faang.school.accountservice.service;

import faang.school.accountservice.config.context.AccountGenerationConfig;
import faang.school.accountservice.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountNumberProvisioningServiceTest {

    @Mock
    private AccountGenerationConfig config;

    @Mock
    private FreeAccountNumberPoolService poolService;

    @Mock
    private AccountNumberGenerator generator;

    @Mock
    private AccountNumberBatchProcessor batchProcessor;

    @InjectMocks
    private AccountNumberProvisioningService provisioningService;

    @BeforeEach
    void setUp() {
        when(config.getMinPoolSize()).thenReturn(50);
    }

    @Test
    @DisplayName("Should provide number from pool when available")
    void shouldProvideNumberFromPoolWhenAvailable() {
        AccountType type = AccountType.DEBIT;
        String pooledNumber = "420000000001";

        when(poolService.retrieveFromPool(type))
                .thenReturn(Optional.of(pooledNumber));

        String result = provisioningService.provideAccountNumber(type);

        assertThat(result).isEqualTo(pooledNumber);
        verify(generator, never()).generateAccountNumber(any());
    }

    @Test
    @DisplayName("Should generate new number when pool is empty")
    void shouldGenerateNewNumberWhenPoolIsEmpty() {
        AccountType type = AccountType.SAVINGS;
        String generatedNumber = "430000000001";

        when(poolService.retrieveFromPool(type))
                .thenReturn(Optional.empty());
        when(generator.generateAccountNumber(type))
                .thenReturn(generatedNumber);

        String result = provisioningService.provideAccountNumber(type);

        assertThat(result).isEqualTo(generatedNumber);
        verify(generator).generateAccountNumber(type);
    }

    @Test
    @DisplayName("Should ensure pool capacity when below minimum")
    void shouldEnsurePoolCapacityWhenBelowMinimum() {
        AccountType type = AccountType.CREDIT;
        int minCount = 100;
        long currentCount = 30;

        when(poolService.countAvailable(type)).thenReturn(currentCount);

        provisioningService.ensurePoolCapacity(type, minCount);

        verify(batchProcessor).generateAndAddToPool(type, 70);
    }

    @Test
    @DisplayName("Should not generate when pool has enough capacity")
    void shouldNotGenerateWhenPoolHasEnoughCapacity() {
        AccountType type = AccountType.BUSINESS;
        int minCount = 50;
        long currentCount = 75;

        when(poolService.countAvailable(type)).thenReturn(currentCount);

        provisioningService.ensurePoolCapacity(type, minCount);

        verify(batchProcessor, never()).generateAndAddToPool(any(), anyInt());
    }

    @Test
    @DisplayName("Should execute operation with account number")
    void shouldExecuteOperationWithAccountNumber() {
        AccountType type = AccountType.DEBIT;
        String accountNumber = "420000000001";
        String expectedResult = "Success";

        when(poolService.retrieveFromPool(type))
                .thenReturn(Optional.of(accountNumber));

        Function<String, String> operation = number -> {
            assertThat(number).isEqualTo(accountNumber);
            return expectedResult;
        };

        String result = provisioningService.executeWithAccountNumber(type, operation);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should propagate exception from operation")
    void shouldPropagateExceptionFromOperation() {
        AccountType type = AccountType.CREDIT;
        String accountNumber = "550000000001";
        RuntimeException exception = new RuntimeException("Operation failed");

        when(poolService.retrieveFromPool(type))
                .thenReturn(Optional.of(accountNumber));

        assertThatThrownBy(() ->
                provisioningService.executeWithAccountNumber(type, number -> {
                    throw exception;
                })
        ).isEqualTo(exception);
    }
}