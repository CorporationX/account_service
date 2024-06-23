package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.jpa.AccountNumbersSequenceJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountNumbersSequenceRepositoryTest {
    @InjectMocks
    private AccountNumbersSequenceRepository repository;
    @Mock
    private AccountNumbersSequenceJpaRepository jpaRepository;

    private final ArgumentCaptor<AccountNumbersSequence> numbersSequenceArgumentCaptor =
            ArgumentCaptor.forClass(AccountNumbersSequence.class);

    @Nested
    class PositiveTest {
        @ParameterizedTest
        @EnumSource(AccountType.class)
        void createAccountNumbersSequenceTest(AccountType type) {
            repository.createAccountNumbersSequence(type);

            verify(jpaRepository).save(numbersSequenceArgumentCaptor.capture());
            AccountNumbersSequence capturedValue = numbersSequenceArgumentCaptor.getValue();
            assertEquals(0L, capturedValue.getSequence());
            assertEquals(type, capturedValue.getType());
            assertEquals(String.valueOf(type.getCode()), capturedValue.getCode());
        }

        @DisplayName("should return true when repository.save doesn't throw exception")
        @Test
        void incrementAccountNumbersSequenceTest() {
            AccountNumbersSequence sequence = AccountNumbersSequence.builder().sequence(0L).build();

            boolean result = repository.incrementAccountNumbersSequence(sequence);

            assertTrue(result);
            verify(jpaRepository).save(any(AccountNumbersSequence.class));
            assertEquals(1L, sequence.getSequence());
        }

        @ParameterizedTest
        @EnumSource(AccountType.class)
        void findOrCreateByTypeTest(AccountType type) {
            AccountNumbersSequence sequence = new AccountNumbersSequence();
            when(jpaRepository.findByType(anyString())).thenReturn(Optional.of(sequence));

            AccountNumbersSequence result = repository.findOrCreateByType(type);

            assertEquals(sequence, result);
            verify(jpaRepository, times(0)).save(any(AccountNumbersSequence.class));
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should return false when repository.save throws exception")
        @Test
        void incrementAccountNumbersSequenceTest() {
            lenient().when(jpaRepository.save(any(AccountNumbersSequence.class))).thenThrow(RuntimeException.class);

            boolean result = repository.incrementAccountNumbersSequence(new AccountNumbersSequence());

            assertFalse(result);
        }

        @ParameterizedTest
        @EnumSource(AccountType.class)
        void findOrCreateByTypeTest(AccountType type) {
            AccountNumbersSequence sequence = new AccountNumbersSequence();
            when(jpaRepository.findByType(anyString())).thenReturn(Optional.empty());
            when(jpaRepository.save(any(AccountNumbersSequence.class))).thenReturn(sequence);

            AccountNumbersSequence result = repository.findOrCreateByType(type);

            assertEquals(sequence, result);
            verify(jpaRepository).save(numbersSequenceArgumentCaptor.capture());
            AccountNumbersSequence capturedValue = numbersSequenceArgumentCaptor.getValue();
            assertEquals(0L, capturedValue.getSequence());
            assertEquals(type, capturedValue.getType());
            assertEquals(String.valueOf(type.getCode()), capturedValue.getCode());
        }
    }
}