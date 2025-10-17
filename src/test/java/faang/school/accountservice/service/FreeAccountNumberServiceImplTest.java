package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.AccountTypeNotInitializedException;
import faang.school.accountservice.exception.UnsupportedAccountTypeException;
import faang.school.accountservice.repository.AccountSequenceRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumberServiceImplTest {
    @InjectMocks
    private FreeAccountNumberServiceImpl freeAccountNumberService;

    @Mock
    private FreeAccountRepository freeAccountRepository;

    @Mock
    private AccountSequenceRepository accountSequenceRepository;

    private static final long DEBIT_ACCOUNT_PREFIX = 4200_0000_0000_0000L;
    private static final long CREDIT_ACCOUNT_PREFIX = 5236_0000_0000_0000L;

    private static final int DEFAULT_BATCH_SIZE = 5;
    private static final long DEFAULT_START_COUNTER = 100L;
    private static final long DEFAULT_VERSION = 1L;

    @Test
    public void generateAccountNumbersSuccess() {
        AccountType type = AccountType.DEBIT;

        AccountNumberSequence sequence = new AccountNumberSequence();
        sequence.setType(type);
        sequence.setCounter(DEFAULT_START_COUNTER);
        sequence.setVersion(DEFAULT_VERSION);

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.of(sequence));

        when(accountSequenceRepository.incrementCounter(
                type, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE))
                .thenReturn(1);

        freeAccountNumberService.generateAccountNumbers(type, DEFAULT_BATCH_SIZE);

        verify(accountSequenceRepository, times(1)).findByType(type);
        verify(accountSequenceRepository, times(1))
                .incrementCounter(type, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE);

        ArgumentCaptor<List<FreeAccountNumber>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(freeAccountRepository, times(1)).saveAll(captor.capture());

        List<FreeAccountNumber> savedNumbers = captor.getValue();

        assertEquals(DEFAULT_BATCH_SIZE, savedNumbers.size());

        for (int i = 0; i < DEFAULT_BATCH_SIZE; i++) {
            long expectedAccountNumber = DEBIT_ACCOUNT_PREFIX + DEFAULT_START_COUNTER + i;
            long actualAccountNumber = savedNumbers.get(i).getId().getAccountNumber();

            assertEquals(expectedAccountNumber, actualAccountNumber);
            assertEquals(type, savedNumbers.get(i).getId().getType());
        }
    }

    @Test
    public void generateAccountNumbersSuccessAfterRetry() {
        AccountType type = AccountType.CREDIT;

        AccountNumberSequence sequence1 = new AccountNumberSequence();
        sequence1.setType(type);
        sequence1.setCounter(DEFAULT_START_COUNTER);
        sequence1.setVersion(DEFAULT_VERSION);

        AccountNumberSequence sequence2 = new AccountNumberSequence();
        sequence2.setType(type);
        sequence2.setCounter(DEFAULT_START_COUNTER + 10);
        sequence2.setVersion(DEFAULT_VERSION + 1);

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.of(sequence1))
                .thenReturn(Optional.of(sequence2));

        when(accountSequenceRepository.incrementCounter(
                type, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE))
                .thenReturn(0);

        when(accountSequenceRepository.incrementCounter(
                type, DEFAULT_START_COUNTER + 10, DEFAULT_VERSION + 1, DEFAULT_BATCH_SIZE))
                .thenReturn(1); // 2-я попытка - УСПЕХ!

        freeAccountNumberService.generateAccountNumbers(type, DEFAULT_BATCH_SIZE);

        verify(accountSequenceRepository, times(2)).findByType(type);

        verify(accountSequenceRepository, times(1))
                .incrementCounter(type, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE);
        verify(accountSequenceRepository, times(1))
                .incrementCounter(type, DEFAULT_START_COUNTER + 10, DEFAULT_VERSION + 1, DEFAULT_BATCH_SIZE);

        ArgumentCaptor<List<FreeAccountNumber>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(freeAccountRepository, times(1)).saveAll(captor.capture());

        List<FreeAccountNumber> savedNumbers = captor.getValue();

        assertEquals(DEFAULT_BATCH_SIZE, savedNumbers.size());

        for (int i = 0; i < DEFAULT_BATCH_SIZE; i++) {
            long expectedAccountNumber = CREDIT_ACCOUNT_PREFIX + (DEFAULT_START_COUNTER + 10) + i;
            long actualAccountNumber = savedNumbers.get(i).getId().getAccountNumber();

            assertEquals(expectedAccountNumber, actualAccountNumber);
            assertEquals(type, savedNumbers.get(i).getId().getType());
        }
    }

    @Test
    public void generateAccountNumbers_whenAccountTypeNotInitialized_throwsException() {
        AccountType type = AccountType.DEBIT;

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.empty());

        AccountTypeNotInitializedException exception = assertThrows(
                AccountTypeNotInitializedException.class,
                () -> freeAccountNumberService.generateAccountNumbers(type, DEFAULT_BATCH_SIZE)
        );

        verify(accountSequenceRepository, times(1)).findByType(type);

        verify(accountSequenceRepository, never()).incrementCounter(any(), anyLong(), anyLong(), anyInt());

        verify(freeAccountRepository, never()).saveAll(any());
    }

    @Test
    public void generateAccountNumbers_whenAllRetriesExhausted_throwsException() {
        AccountType type = AccountType.CREDIT;

        AccountNumberSequence sequence = new AccountNumberSequence();
        sequence.setType(type);
        sequence.setCounter(DEFAULT_START_COUNTER);
        sequence.setVersion(DEFAULT_VERSION);

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.of(sequence));

        when(accountSequenceRepository.incrementCounter(
                type, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE))
                .thenReturn(0);

        AccountNumberGenerationException exception = assertThrows(
                AccountNumberGenerationException.class,
                () -> freeAccountNumberService.generateAccountNumbers(type, DEFAULT_BATCH_SIZE)
        );

        assertTrue(exception.getMessage().contains("Failed to generate batch account numbers"),
                "Сообщение должно содержать информацию о провале генерации");
        assertTrue(exception.getMessage().contains("10 attempts"),
                "Сообщение должно содержать количество попыток");
        assertTrue(exception.getMessage().contains(type.name()),
                "Сообщение должно содержать тип счёта");

        verify(accountSequenceRepository, times(10)).findByType(type);

        verify(accountSequenceRepository, times(10))
                .incrementCounter(type, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE);

        verify(freeAccountRepository, never()).saveAll(any());
    }

    @Test
    public void generateAccountNumbers_whenUnsupportedAccountType_throwsException() {
        AccountType unsupportedType = AccountType.ACCOUNT_FOR_INDIVIDUALS;

        AccountNumberSequence sequence = new AccountNumberSequence();
        sequence.setType(unsupportedType);
        sequence.setCounter(DEFAULT_START_COUNTER);
        sequence.setVersion(DEFAULT_VERSION);

        when(accountSequenceRepository.findByType(unsupportedType))
                .thenReturn(Optional.of(sequence));

        when(accountSequenceRepository.incrementCounter(
                unsupportedType, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE))
                .thenReturn(1);

        UnsupportedAccountTypeException exception = assertThrows(
                UnsupportedAccountTypeException.class,
                () -> freeAccountNumberService.generateAccountNumbers(unsupportedType, DEFAULT_BATCH_SIZE)
        );

        assertTrue(exception.getMessage().contains("ACCOUNT_FOR_INDIVIDUALS"),
                "Сообщение должно содержать информацию о неподдерживаемом типе");

        verify(accountSequenceRepository, times(1)).findByType(unsupportedType);
        verify(accountSequenceRepository, times(1))
                .incrementCounter(unsupportedType, DEFAULT_START_COUNTER, DEFAULT_VERSION, DEFAULT_BATCH_SIZE);

        verify(freeAccountRepository, never()).saveAll(any());
    }

    @Test
    public void retrieveAccountNumber_whenFreeNumberExists_returnsAndDeletesIt() {
        AccountType type = AccountType.DEBIT;
        long expectedAccountNumber = DEBIT_ACCOUNT_PREFIX + 12345L;

        FreeAccountId freeAccountId = new FreeAccountId(type, expectedAccountNumber);

        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(freeAccountId);

        when(freeAccountRepository.findFirstByType(type.name()))
                .thenReturn(Optional.of(freeAccountNumber));

        Long result = freeAccountNumberService.retrieveAccountNumber(type);

        assertEquals(expectedAccountNumber, result,
                "Должен вернуться номер из репозитория");

        verify(freeAccountRepository, times(1)).findFirstByType(type.name());

        verify(freeAccountRepository, times(1)).delete(freeAccountNumber);

        verify(accountSequenceRepository, never()).findByType(any());
        verify(accountSequenceRepository, never()).incrementCounter(any(), anyLong(), anyLong(), anyInt());
    }

    @Test
    public void retrieveAccountNumber_whenNoFreeNumbers_generatesNewOne() {
        AccountType type = AccountType.CREDIT;

        AccountNumberSequence sequence = new AccountNumberSequence();
        sequence.setType(type);
        sequence.setCounter(DEFAULT_START_COUNTER);
        sequence.setVersion(DEFAULT_VERSION);

        when(freeAccountRepository.findFirstByType(type.name()))
                .thenReturn(Optional.empty());

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.of(sequence));

        when(accountSequenceRepository.incrementCounter(
                type, DEFAULT_START_COUNTER, DEFAULT_VERSION, 1))
                .thenReturn(1);

        Long result = freeAccountNumberService.retrieveAccountNumber(type);

        long expectedAccountNumber = CREDIT_ACCOUNT_PREFIX + DEFAULT_START_COUNTER;

        assertEquals(expectedAccountNumber, result,
                "Должен вернуться новый сгенерированный номер");

        verify(freeAccountRepository, times(1)).findFirstByType(type.name());

        verify(accountSequenceRepository, times(1)).findByType(type);
        verify(accountSequenceRepository, times(1))
                .incrementCounter(type, DEFAULT_START_COUNTER, DEFAULT_VERSION, 1);

        verify(freeAccountRepository, never()).delete(any());

        verify(freeAccountRepository, never()).saveAll(any());
    }

    @Test
    public void retrieveAccountNumber_whenAccountTypeNotInitialized_throwsException() {
        AccountType type = AccountType.DEBIT;

        when(freeAccountRepository.findFirstByType(type.name()))
                .thenReturn(Optional.empty());

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.empty());

        AccountTypeNotInitializedException exception = assertThrows(
                AccountTypeNotInitializedException.class,
                () -> freeAccountNumberService.retrieveAccountNumber(type)
        );

        verify(freeAccountRepository, times(1)).findFirstByType(type.name());

        verify(accountSequenceRepository, times(1)).findByType(type);

        verify(accountSequenceRepository, never()).incrementCounter(any(), anyLong(), anyLong(), anyInt());
        verify(freeAccountRepository, never()).delete(any());
        verify(freeAccountRepository, never()).saveAll(any());
    }

    @Test
    public void retrieveAccountNumber_whenAllRetriesExhausted_throwsException() {
        AccountType type = AccountType.CREDIT;

        AccountNumberSequence sequence = new AccountNumberSequence();
        sequence.setType(type);
        sequence.setCounter(DEFAULT_START_COUNTER);
        sequence.setVersion(DEFAULT_VERSION);

        when(freeAccountRepository.findFirstByType(type.name()))
                .thenReturn(Optional.empty());

        when(accountSequenceRepository.findByType(type))
                .thenReturn(Optional.of(sequence));

        when(accountSequenceRepository.incrementCounter(
                type, DEFAULT_START_COUNTER, DEFAULT_VERSION, 1))
                .thenReturn(0);

        AccountNumberGenerationException exception = assertThrows(
                AccountNumberGenerationException.class,
                () -> freeAccountNumberService.retrieveAccountNumber(type)
        );

        assertTrue(exception.getMessage().contains("Failed to generate single account number"),
                "Сообщение должно содержать информацию о провале генерации одиночного номера");
        assertTrue(exception.getMessage().contains("10 attempts"),
                "Сообщение должно содержать количество попыток");
        assertTrue(exception.getMessage().contains(type.name()),
                "Сообщение должно содержать тип счёта");

        verify(freeAccountRepository, times(1)).findFirstByType(type.name());

        verify(accountSequenceRepository, times(10)).findByType(type);
        verify(accountSequenceRepository, times(10))
                .incrementCounter(type, DEFAULT_START_COUNTER, DEFAULT_VERSION, 1);

        verify(freeAccountRepository, never()).delete(any());
        verify(freeAccountRepository, never()).saveAll(any());
    }
}
