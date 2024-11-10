package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.service.account.numbers.sequence.DigitSequenceManager;
import faang.school.accountservice.service.account.numbers.sequence.IDigitSequenceGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DigitSequenceManagerTest {

    @Mock
    private IDigitSequenceGenerator digitSequenceGenerator;

    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @InjectMocks
    private DigitSequenceManager digitSequenceManager;

    @Test
    public void testTryStartGenerationNumberForPool_whenGenerationNotNeeded() {
        AccountNumberType type = AccountNumberType.DEBIT;
        when(digitSequenceGenerator.isGenerationNeeded(type)).thenReturn(false);

        digitSequenceManager.tryStartGenerationNumberForPool(type);

        verify(digitSequenceGenerator, never()).generationSequencesAsync(any());
    }

    @Test
    public void testTryStartGenerationNumberForPool_whenPermissionToStartGenerationIsNotGiven() {
        AccountNumberType type = AccountNumberType.DEBIT;
        when(digitSequenceGenerator.isGenerationNeeded(type)).thenReturn(true);
        when(accountNumbersSequenceRepository.findAccountUniqueNumberCounterByType(any())).thenReturn(Optional.empty());

        digitSequenceManager.tryStartGenerationNumberForPool(type);

        verify(digitSequenceGenerator, never()).generationSequencesAsync(any());
    }

    @Test
    public void testTryStartGenerationNumberForPool_whenNoLockedCounterFound() {
        AccountNumberType type = AccountNumberType.DEBIT;
        digitSequenceManager.tryStartGenerationNumberForPool(type);

        verify(digitSequenceGenerator, never()).generationSequencesAsync(any());
    }

    @Test
    public void testTryStartGenerationNumberForPool_whenAllConditionsAreMet() {
        AccountNumberType type = AccountNumberType.DEBIT;
        AccountUniqueNumberCounter account = new AccountUniqueNumberCounter();
        account.setGenerationState(false);
        when(digitSequenceGenerator.isGenerationNeeded(type)).thenReturn(true);
        when(accountNumbersSequenceRepository.findAccountUniqueNumberCounterByType(type.toString())).thenReturn(Optional.of(account));
        when(accountNumbersSequenceRepository.tryLockCounterByTypeForUpdate(type.toString())).thenReturn(Optional.of(new AccountUniqueNumberCounter()));

        digitSequenceManager.tryStartGenerationNumberForPool(type);

        verify(digitSequenceGenerator).generationSequencesAsync(eq(type));
    }

    @Test
    public void testGetAndRemoveFreeAccountNumberByType_whenAccountNumberIsPresent() {
        AccountNumberType type = AccountNumberType.DEBIT;
        when(digitSequenceGenerator.getAndRemoveFreeAccountNumberByType(type)).thenReturn(Optional.of("67890"));

        Optional<String> result = digitSequenceManager.getAndRemoveFreeAccountNumberByType(type);

        assertTrue(result.isPresent());
        assertEquals("67890", result.get());
    }

    @Test
    public void testGetAndRemoveFreeAccountNumberByType_whenAccountNumberIsNotPresent() {
        AccountNumberType type = AccountNumberType.DEBIT;
        when(digitSequenceGenerator.getAndRemoveFreeAccountNumberByType(type)).thenReturn(Optional.empty());

        Optional<String> result = digitSequenceManager.getAndRemoveFreeAccountNumberByType(type);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGenerateNewAccountNumberWithoutPool_whenNumberIsGenerated() {
        AccountNumberType type = AccountNumberType.DEBIT;
        when(digitSequenceGenerator.generateNewAccountNumberWithoutPool(type)).thenReturn("54321");

        String result = digitSequenceManager.generateNewAccountNumberWithoutPool(type);
    }
}


