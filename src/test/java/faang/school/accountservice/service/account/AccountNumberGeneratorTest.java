package faang.school.accountservice.service.account;

import faang.school.accountservice.repository.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AccountNumberGeneratorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountNumberGenerator accountNumberGenerator;

    private final int accountNumberLength = 12;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(accountNumberGenerator, "accountNumberLength", accountNumberLength);
    }

    @Test
    void testGenerateAccountNumber_UniqueNumber() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        String generatedAccountNumber = accountNumberGenerator.generateAccountNumber();

        assertEquals(accountNumberLength, generatedAccountNumber.length());
        verify(accountRepository, times(1)).existsByAccountNumber(generatedAccountNumber);
    }

    @Test
    void testGenerateAccountNumber_NumberAlreadyExists() {
        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        String generatedAccountNumber = accountNumberGenerator.generateAccountNumber();

        assertEquals(accountNumberLength, generatedAccountNumber.length());
        verify(accountRepository, times(2)).existsByAccountNumber(anyString());
    }

    @Test
    void testGenerateAccountNumber_Length() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        String generatedAccountNumber = accountNumberGenerator.generateAccountNumber();

        assertEquals(accountNumberLength, generatedAccountNumber.length());
        assertTrue(generatedAccountNumber.chars().anyMatch(ch -> Character.isDigit(ch)));
    }
}