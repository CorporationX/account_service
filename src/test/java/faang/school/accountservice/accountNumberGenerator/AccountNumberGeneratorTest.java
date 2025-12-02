package faang.school.accountservice.accountNumberGenerator;

import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountNumberGeneratorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountNumberGenerator accountNumberGenerator;

    @Test
    @DisplayName("Генерация уникального номера - успех с первой попытки")
    void testGenerate_SuccessFirstAttempt() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        // When
        String accountNumber = accountNumberGenerator.generate();

        // Then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).hasSize(20);
        assertThat(accountNumber).matches("\\d{20}");
        assertThat(accountNumber.charAt(0)).isNotEqualTo('0'); // первая цифра не 0

        verify(accountRepository, times(1)).findByAccountNumber(anyString());
    }

    @Test
    @DisplayName("Генерация уникального номера - коллизия, успех со второй попытки")
    void testGenerate_SuccessAfterCollision() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn((Optional) Optional.of(mock(Object.class))) // первая попытка - коллизия
                .thenReturn(Optional.empty()); // вторая попытка - успех

        // When
        String accountNumber = accountNumberGenerator.generate();

        // Then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).hasSize(20);
        assertThat(accountNumber).matches("\\d{20}");

        verify(accountRepository, times(2)).findByAccountNumber(anyString());
    }

    @Test
    @DisplayName("Генерация уникального номера - множественные коллизии")
    void testGenerate_MultipleCollisions() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn((Optional) Optional.of(mock(Object.class))) // попытки 1-5 - коллизии
                .thenReturn((Optional) Optional.of(mock(Object.class)))
                .thenReturn((Optional) Optional.of(mock(Object.class)))
                .thenReturn((Optional) Optional.of(mock(Object.class)))
                .thenReturn((Optional) Optional.of(mock(Object.class)))
                .thenReturn(Optional.empty()); // попытка 6 - успех

        // When
        String accountNumber = accountNumberGenerator.generate();

        // Then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).hasSize(20);

        verify(accountRepository, times(6)).findByAccountNumber(anyString());
    }

    @Test
    @DisplayName("Генерация уникального номера - превышен лимит попыток")
    void testGenerate_ExceedsMaxAttempts() {
        // Given - все попытки заканчиваются коллизией
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn((Optional) Optional.of(mock(Object.class)));

        // When & Then
        assertThatThrownBy(() -> accountNumberGenerator.generate())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to generate unique account number")
                .hasMessageContaining("100 attempts");

        verify(accountRepository, times(100)).findByAccountNumber(anyString());
    }

    @Test
    @DisplayName("Проверка существования номера - номер существует")
    void testExists_NumberExists() {
        // Given
        String accountNumber = "12345678901234567890";
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn((Optional) Optional.of(mock(Object.class)));

        // When
        boolean exists = accountNumberGenerator.exists(accountNumber);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Проверка существования номера - номер не существует")
    void testExists_NumberDoesNotExist() {
        // Given
        String accountNumber = "12345678901234567890";
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        // When
        boolean exists = accountNumberGenerator.exists(accountNumber);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Генерация нескольких уникальных номеров - все разные")
    void testGenerate_MultipleNumbersAreUnique() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        // When
        String number1 = accountNumberGenerator.generate();
        String number2 = accountNumberGenerator.generate();
        String number3 = accountNumberGenerator.generate();

        // Then
        assertThat(number1).isNotEqualTo(number2);
        assertThat(number1).isNotEqualTo(number3);
        assertThat(number2).isNotEqualTo(number3);
    }

    @Test
    @DisplayName("Первая цифра номера никогда не равна 0")
    void testGenerate_FirstDigitNeverZero() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        // When - генерируем несколько номеров
        for (int i = 0; i < 100; i++) {
            String accountNumber = accountNumberGenerator.generate();

            // Then
            assertThat(accountNumber.charAt(0)).isNotEqualTo('0');
        }
    }

    @Test
    @DisplayName("Номер содержит только цифры")
    void testGenerate_ContainsOnlyDigits() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        // When
        String accountNumber = accountNumberGenerator.generate();

        // Then
        assertThat(accountNumber).matches("^[1-9]\\d{19}$");
    }

    @Test
    @DisplayName("Длина номера всегда 20 символов")
    void testGenerate_LengthAlways20() {
        // Given
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        // When - генерируем несколько номеров
        for (int i = 0; i < 50; i++) {
            String accountNumber = accountNumberGenerator.generate();

            // Then
            assertThat(accountNumber).hasSize(20);
        }
    }
}