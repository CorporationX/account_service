package faang.school.accountservice.service;

import faang.school.accountservice.config.context.AccountGenerationConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.NoAvailableAccountNumberException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountNumberGeneratorTest {

    @Mock
    private AccountNumbersSequenceRepository sequenceRepository;

    @Mock
    private AccountGenerationConfig config;

    @InjectMocks
    private AccountNumberGenerator generator;

    @BeforeEach
    void setUp() {
        when(config.getMaxRetryAttempts()).thenReturn(5);
    }

    @Test
    @DisplayName("Should generate valid account number")
    void shouldGenerateValidAccountNumber() {
        AccountType accountType = AccountType.DEBIT;
        Long sequence = 12345L;

        when(sequenceRepository.getNextSequenceValue(accountType, 5))
                .thenReturn(sequence);

        String result = generator.generateAccountNumber(accountType);

        assertThat(result).isEqualTo("420000012345");
        assertThat(accountType.isValidAccountNumber(result)).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when sequence exceeds maximum")
    void shouldThrowExceptionWhenSequenceExceedsMaximum() {
        AccountType accountType = AccountType.DEBIT;
        Long invalidSequence = accountType.getMaxSequenceValue() + 1;

        when(sequenceRepository.getNextSequenceValue(accountType, 5))
                .thenReturn(invalidSequence);

        assertThatThrownBy(() -> generator.generateAccountNumber(accountType))
                .isInstanceOf(NoAvailableAccountNumberException.class)
                .hasMessageContaining("exceeds maximum");
    }

    @Test
    @DisplayName("Should generate batch of account numbers")
    void shouldGenerateBatchOfAccountNumbers() {
        AccountType accountType = AccountType.SAVINGS;
        int count = 3;
        Long startSequence = 100L;

        when(sequenceRepository.reserveSequenceBlock(accountType, count, 5))
                .thenReturn(startSequence);

        List<String> result = generator.generateBatch(accountType, count);

        assertThat(result).hasSize(count);
        assertThat(result).containsExactly(
                "430000000100",
                "430000000101",
                "430000000102"
        );
    }
}