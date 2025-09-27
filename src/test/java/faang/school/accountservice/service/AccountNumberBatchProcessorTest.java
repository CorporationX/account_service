package faang.school.accountservice.service;

import faang.school.accountservice.config.context.AccountGenerationConfig;
import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountNumberBatchProcessorTest {

    @Mock
    private AccountGenerationConfig config;

    @Mock
    private AccountNumberGenerator generator;

    @Mock
    private FreeAccountNumberPoolService poolService;

    @Mock
    private FreeAccountNumbersRepository repository;

    @InjectMocks
    private AccountNumberBatchProcessor batchProcessor;

    @BeforeEach
    void setUp() {
        when(config.getMaxDuplicateRetries()).thenReturn(3);
    }

    @Test
    @DisplayName("Should generate and add unique numbers to pool")
    void shouldGenerateAndAddUniqueNumbersToPool() {
        AccountType type = AccountType.DEBIT;
        int count = 3;
        List<String> generatedNumbers = Arrays.asList(
                "420000000001", "420000000002", "420000000003"
        );

        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(generator.generateBatch(type, count)).thenReturn(generatedNumbers);
        when(repository.saveAll(anyList())).thenAnswer(invocation ->
                invocation.getArgument(0));

        List<FreeAccountNumbers> result = batchProcessor.generateAndAddToPool(type, count);

        assertThat(result).hasSize(count);
        assertThat(result.stream().map(FreeAccountNumbers::getAccountNumber))
                .containsExactlyElementsOf(generatedNumbers);
    }

    @Test
    @DisplayName("Should handle duplicates with individual save")
    void shouldHandleDuplicatesWithIndividualSave() {
        AccountType type = AccountType.CREDIT;
        int count = 2;
        List<String> numbers = Arrays.asList("550000000001", "550000000002");

        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(generator.generateBatch(type, count)).thenReturn(numbers);
        when(repository.saveAll(anyList()))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        when(repository.save(any())).thenAnswer(invocation -> {
            FreeAccountNumbers arg = invocation.getArgument(0);
            if (arg.getAccountNumber().equals("550000000001")) {
                return arg;
            }
            throw new DataIntegrityViolationException("Duplicate");
        });

        List<FreeAccountNumbers> result = batchProcessor.generateAndAddToPool(type, count);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountNumber()).isEqualTo("550000000001");
        verify(repository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should filter existing numbers")
    void shouldFilterExistingNumbers() {
        AccountType type = AccountType.SAVINGS;
        int count = 3;
        String existingNumber = "430000000002";

        FreeAccountNumbers existing = new FreeAccountNumbers(type, existingNumber);
        when(repository.findAll()).thenReturn(List.of(existing));

        List<String> generated = Arrays.asList(
                "430000000001", "430000000002", "430000000003"
        );
        when(generator.generateBatch(type, count)).thenReturn(generated);
        when(repository.saveAll(anyList())).thenAnswer(invocation ->
                invocation.getArgument(0));

        List<FreeAccountNumbers> result = batchProcessor.generateAndAddToPool(type, count);

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(FreeAccountNumbers::getAccountNumber))
                .containsExactly("430000000001", "430000000003");
    }
}