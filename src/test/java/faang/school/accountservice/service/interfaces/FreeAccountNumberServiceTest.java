package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.entity.AccountSequence;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.free_account_numbers_exception.AccountNumberException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import faang.school.accountservice.service.implementations.FreeAccountNumberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumberServiceTest {

    private static final String FIXED_PREFIX = "1111222233334444";
    private final AccountType accountType = AccountType.INDIVIDUAL;

    private AccountType type;
    private AccountSequence sequence;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private FreeAccountNumberServiceImpl freeAccountNumberService;

    @Mock
    private FreeAccountNumberRepository freeAccountNumberRepository;

    @Mock
    private AccountNumbersSequenceRepository sequenceRepository;

    @InjectMocks
    private FreeAccountNumberServiceImpl service;

    @BeforeEach
    void setUp() {
        type = AccountType.LEGAL;
        sequence = new AccountSequence();
        sequence.setType(type);
        sequence.setCounter(0L);
        sequence.setVersion(0L);

        reset(freeAccountNumberRepository, sequenceRepository, accountRepository);
    }

    @Test
    void testGenerateAccountNumberSuccessFirstAttempt() {
        String expectedNumber = FIXED_PREFIX + String.format("%04d", getCurrentSequence());
        when(accountRepository.existsByAccountNumber(expectedNumber)).thenReturn(false);

        String result = freeAccountNumberService.generateAccountNumber(accountType);

        assertEquals(20, result.length());
        assertTrue(result.matches("\\d{20}"));
        assertTrue(result.startsWith(FIXED_PREFIX));
        verify(accountRepository, times(1)).existsByAccountNumber(result);
    }

    @Test
    void testGenerateAccountNumberSuccessAfterCollision() {
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
    void testGenerateAccountNumberFailureAfterMaxAttempts() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> freeAccountNumberService.generateAccountNumber(accountType)
        );
        assertEquals("Failed to generate unique account number after 100 attempts",
                exception.getMessage());
        verify(accountRepository, times(100)).existsByAccountNumber(anyString());
    }

    @Test
    void testGenerateAccountNumberAccountTypeDoesNotAffectNumber() {
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

    /**************************************************************************************************/
    @Test
    void testGenerateAccountNumbersSuccess() {
        int batchSize = 2;
        when(sequenceRepository.findById(type)).thenReturn(Optional.of(sequence));
        when(sequenceRepository.incrementCounterIfMatch(type, batchSize, 0L, 0L))
                .thenReturn(true);
        when(freeAccountNumberRepository.existsById(any(FreeAccountId.class))).thenReturn(false);

        service.generateAccountNumbers(type, batchSize);

        verify(sequenceRepository, times(1))
                .incrementCounterIfMatch(type, batchSize, 0L, 0L);
        verify(freeAccountNumberRepository, times(1))
                .saveAll(argThat((List<FreeAccountNumber> numbers) -> {
                    assertEquals(2, numbers.size());
                    assertEquals(2000000000000001L, numbers.get(0).getId().getAccountNumber());
                    assertEquals(2000000000000002L, numbers.get(1).getId().getAccountNumber());
                    return true;
                }));
    }

    @Test
    void testGenerateAccountNumbersSequenceNotFound() {
        when(sequenceRepository.findById(type)).thenReturn(Optional.empty());

        assertThrows(AccountNumberException.class, () -> service.generateAccountNumbers(type, 1));
    }

    @Test
    void testGenerateOneAccountNumber() {
        when(sequenceRepository.findById(type)).thenReturn(Optional.of(sequence));
        when(sequenceRepository.incrementCounterIfMatch(type, 1, 0L, 0L))
                .thenReturn(true);
        when(freeAccountNumberRepository.existsById(any(FreeAccountId.class))).thenReturn(false);

        service.generateOneAccountNumber(type);

        verify(sequenceRepository, times(1)).incrementCounterIfMatch(type, 1, 0L, 0L);
        verify(freeAccountNumberRepository, times(1))
                .saveAll(argThat((List<FreeAccountNumber> numbers) -> {
                    assertEquals(1, numbers.size());
                    assertEquals(2000000000000001L, numbers.get(0).getId().getAccountNumber());
                    return true;
                }));
    }

    @Test
    void testUseFreeAccountNumberSuccessWithExistingNumber() {
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(new FreeAccountId(type, 2000000000000001L));
        List<Object[]> firstResult = new ArrayList<>();
        firstResult.add(new Object[]{2000000000000001L, type.name()});
        List<Object[]> emptyResult = new ArrayList<>();

        when(freeAccountNumberRepository.retrieveFirst(type.name()))
                .thenReturn(firstResult)
                .thenReturn(emptyResult);

        Consumer<Long> action = mock(Consumer.class);

        service.useFreeAccountNumber(type, action);

        verify(freeAccountNumberRepository, times(1)).retrieveFirst(type.name());
        verify(action, times(1)).accept(2000000000000001L);
    }

    @Test
    void testUseFreeAccountNumberGenerateNewNumberIfNoneExists() {
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(new FreeAccountId(type, 2000000000000001L));
        List<Object[]> firstResult = new ArrayList<>();
        firstResult.add(new Object[]{2000000000000001L, type.name()});
        List<Object[]> emptyResult = new ArrayList<>();

        when(freeAccountNumberRepository.retrieveFirst(type.name()))
                .thenReturn(emptyResult)
                .thenReturn(firstResult);

        when(sequenceRepository.findById(type)).thenReturn(Optional.of(sequence));
        when(sequenceRepository.incrementCounterIfMatch(type, 1, 0L, 0L)).thenReturn(true);
        when(freeAccountNumberRepository.existsById(any(FreeAccountId.class))).thenReturn(false);
        Consumer<Long> action = mock(Consumer.class);

        service.useFreeAccountNumber(type, action);

        verify(freeAccountNumberRepository, times(1)).saveAll(anyList());
        verify(freeAccountNumberRepository, times(2)).retrieveFirst(type.name()); // Два вызова: до и после генерации
        verify(action, times(1)).accept(2000000000000001L);
    }
}