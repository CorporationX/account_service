package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.implementations.FreeAccountNumberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumberServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private FreeAccountNumberServiceImpl freeAccountNumberService;

    private final String FIXED_PREFIX = "1111222233334444";
    private final AccountType accountType = AccountType.INDIVIDUAL;

    @Test
    void testGenerateAccountNumber_Success_FirstAttempt() {
        String expectedNumber = FIXED_PREFIX + String.format("%04d", getCurrentSequence());
        when(accountRepository.existsByAccountNumber(expectedNumber)).thenReturn(false);

        String result = freeAccountNumberService.generateAccountNumber(accountType);

        assertEquals(20, result.length());
        assertTrue(result.matches("\\d{20}"));
        assertTrue(result.startsWith(FIXED_PREFIX));
        verify(accountRepository, times(1)).existsByAccountNumber(result);
    }

    @Test
    void testGenerateAccountNumber_Success_AfterCollision() {
        String firstNumber = FIXED_PREFIX + String.format("%04d", getCurrentSequence());
        String secondNumber = FIXED_PREFIX + String.format("%04d", getCurrentSequence() + 1);
        when(accountRepository.existsByAccountNumber(firstNumber)).thenReturn(true);
        when(accountRepository.existsByAccountNumber(secondNumber)).thenReturn(false);

        String result = freeAccountNumberService.generateAccountNumber(accountType);

        assertEquals(secondNumber, result);
        assertEquals(20, result.length());
        verify(accountRepository, times(1)).existsByAccountNumber(firstNumber);
        verify(accountRepository, times(1)).existsByAccountNumber(secondNumber);
    }

    @Test
    void testGenerateAccountNumber_Failure_AfterMaxAttempts() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> freeAccountNumberService.generateAccountNumber(accountType)
        );
        assertEquals("Failed to generate unique account number after 100 attempts", exception.getMessage());
        verify(accountRepository, times(100)).existsByAccountNumber(anyString());
    }

    @Test
    void testGenerateAccountNumber_AccountTypeDoesNotAffectNumber() {
        String expectedNumber = FIXED_PREFIX + String.format("%04d", getCurrentSequence());
        when(accountRepository.existsByAccountNumber(expectedNumber)).thenReturn(false);

        String resultChecking = freeAccountNumberService.generateAccountNumber(AccountType.INDIVIDUAL);
        String resultSavings = freeAccountNumberService.generateAccountNumber(AccountType.SAVINGS);

        assertEquals(20, resultChecking.length());
        assertEquals(20, resultSavings.length());
        assertTrue(resultChecking.startsWith(FIXED_PREFIX));
        assertTrue(resultSavings.startsWith(FIXED_PREFIX));
        verify(accountRepository, times(2)).existsByAccountNumber(anyString());
    }

    private int getCurrentSequence() {
        try {
            java.lang.reflect.Field sequenceField = FreeAccountNumberServiceImpl.class.getDeclaredField("sequence");
            sequenceField.setAccessible(true);
            java.util.concurrent.atomic.AtomicInteger sequence = (java.util.concurrent.atomic.AtomicInteger)
                    sequenceField.get(null);
            return sequence.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to access sequence field", e);
        }
    }
}