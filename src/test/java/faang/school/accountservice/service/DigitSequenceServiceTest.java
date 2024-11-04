package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.account.numbers.AccountNumberConfig;
import faang.school.accountservice.service.account.numbers.DigitSequenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DigitSequenceServiceTest {
    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private AccountNumberConfig accountNumberConfig;

    @InjectMocks
    private DigitSequenceService digitSequenceService;

    private AccountNumberType type;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        type = AccountNumberType.BUSINESS; // Установка типа счета
    }

    @Test
    void testCheckForGenerationSequencesAsync_whenCounterIsAbsent() {
        when(accountNumbersSequenceRepository.findByTypeForUpdate(type.toString())).thenReturn(Optional.empty());

        digitSequenceService.checkForGenerationSequencesAsync(type);

        verify(accountNumbersSequenceRepository, never()).setActiveGenerationState(anyString());
    }

    @Test
    void testTryActivateGenerationState_whenGenerationIsInactive() {
        AccountUniqueNumberCounter counter = new AccountUniqueNumberCounter();
        counter.setGeneration_state(false);
        digitSequenceService.tryActivateGenerationState(type, counter);
    }

    @Test
    void testGenerateAndSaveAccountNumbers_whenCountIsBelowThreshold() {
        when(freeAccountNumbersRepository.countFreeAccountNumberByType(type)).thenReturn(0L);
        when(accountNumberConfig.getMinNumberOfFreeAccounts()).thenReturn(1);
        when(accountNumberConfig.getMaxNumberOfFreeAccounts()).thenReturn(10);
        when(accountNumberConfig.getMaxlengthOfDigitSequence()).thenReturn(15);

        digitSequenceService.generateAndSaveAccountNumbers(type);

        verify(freeAccountNumbersRepository).saveAll(anyList());
        verify(accountNumbersSequenceRepository).upCounterAndGet(type.toString(), 9);
    }

    @Test
    void testGetAndRemoveFreeAccountNumberByType_whenAccountNumberExists() {
        String accountNumber = "BUS-0000001";
        when(freeAccountNumbersRepository.getFreeAccountNumberByType(type)).thenReturn(Optional.of(accountNumber));
        when(freeAccountNumbersRepository.removeFreeAccountNumber(accountNumber)).thenReturn(1);

        Optional<String> result = digitSequenceService.getAndRemoveFreeAccountNumberByType(type);

        assertTrue(result.isPresent());
        assertEquals(accountNumber, result.get());
        verify(freeAccountNumbersRepository).removeFreeAccountNumber(accountNumber);
    }

    @Test
    void testGenerateNewAccountNumberWithoutPool() {
        when(accountNumbersSequenceRepository.incrementAndGet(type.toString())).thenReturn(1L);
        when(accountNumberConfig.getMaxlengthOfDigitSequence()).thenReturn(20);

        String accountNumber = digitSequenceService.generateNewAccountNumberWithoutPool(type);

        assertEquals("42600000000000000001", accountNumber);
    }
}