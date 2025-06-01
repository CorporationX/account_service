package faang.school.accountservice.service;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.exception.accountnumber.AccountNumberSequenceNotFoundException;
import faang.school.accountservice.exception.accountnumber.NoAvailableAccountNumberException;
import faang.school.accountservice.exception.accountnumber.UnknownAccountNumberTypeException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of FreeAccountNumbersServiceImplTest")
public class FreeAccountNumbersServiceImplTest {

    private static final long TEST_ACCOUNT_NUMBER = 1L;
    private static final int BATCH_SIZE = 10;
    private static final int FALLBACK_BATCH_SIZE = 5;
    private static final int DEBIT_NUMBER_PREFIX = 4200;
    private static final long ACCOUNT_NUMBER_MULTIPLIER = 1_000_000_000_000L;

    private final Map<AccountNumberType, Integer> prefixes = new HashMap<>();

    @Mock
    private AccountNumbersSequenceRepository sequenceRepository;

    @Mock
    private FreeAccountNumbersRepository accountNumbersRepository;

    @Mock
    private AccountNumberProperties properties;

    @Mock
    private Consumer<FreeAccountNumber> numberConsumer;

    @Spy
    @InjectMocks
    private FreeAccountNumbersServiceImpl service;

    @Captor
    private ArgumentCaptor<List<FreeAccountNumber>> numberListCaptor;

    @Captor
    private ArgumentCaptor<FreeAccountNumber> numberCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(service, "fallbackBatchSize", FALLBACK_BATCH_SIZE);
        prefixes.put(AccountNumberType.DEBIT, DEBIT_NUMBER_PREFIX);
    }

    @Test
    @DisplayName("generateAccountNumbers - unknown account number type")
    public void testGenerateAccountNumbersWithUnknownType() {
        Exception exception = assertThrows(UnknownAccountNumberTypeException.class,
                () -> service.generateAccountNumbers(AccountNumberType.DEBIT, BATCH_SIZE));

        assertEquals("Unknown account number type: DEBIT", exception.getMessage());
    }

    @Test
    @DisplayName("generateAccountNumbers - sequence not found by type")
    public void TestGenerateAccountNumbersNotFoundSequence() {
        when(properties.getPrefixes()).thenReturn(prefixes);

        Exception exception = assertThrows(AccountNumberSequenceNotFoundException.class,
                () -> service.generateAccountNumbers(AccountNumberType.DEBIT, BATCH_SIZE));

        assertEquals("Sequence not found by 'DEBIT' type", exception.getMessage());
    }

    @Test
    @DisplayName("generateAccountNumbers - successfully")
    public void testGeneratedAccountNumbersSuccessfully() {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setType(AccountNumberType.DEBIT);
        sequence.setCounter(1L);

        when(properties.getPrefixes()).thenReturn(prefixes);
        when(sequenceRepository.findByTypeWithOptimisticLock(AccountNumberType.DEBIT))
                .thenReturn(Optional.of(sequence));

        service.generateAccountNumbers(AccountNumberType.DEBIT, BATCH_SIZE);

        verify(accountNumbersRepository, times(1)).saveAll(numberListCaptor.capture());
        List<FreeAccountNumber> numbers = numberListCaptor.getValue();
        assertNotNull(numbers);
        assertEquals(10, numbers.size());
        numbers.forEach(number -> {
                    assertEquals(AccountNumberType.DEBIT, number.getId().getType());
                    assertEquals(DEBIT_NUMBER_PREFIX, number.getId().getAccountNumber() / ACCOUNT_NUMBER_MULTIPLIER);
                });
    }

    @Test
    @DisplayName("receiveAccountNumber - empty number after generation")
    public void testReceiveAccountNumberWithEmptyNumber() {
        doNothing().when(service).generateAccountNumbers(AccountNumberType.DEBIT, FALLBACK_BATCH_SIZE);

        Exception exception = assertThrows(NoAvailableAccountNumberException.class,
                () -> service.receiveAccountNumber(AccountNumberType.DEBIT, System.out::println));

        assertEquals("Failed to obtain account number after generation", exception.getMessage());
    }

    @Test
    @DisplayName("receiveAccountNumber - successfully without generating numbers")
    public void testReceiveAccountNumberSuccessfullyWithoutGeneration() {
        FreeAccountNumber number = getFreeAccountNumber();

        when(accountNumbersRepository.findFirstByTypeForUpdate(AccountNumberType.DEBIT.name()))
                .thenReturn(Optional.of(number));

        service.receiveAccountNumber(AccountNumberType.DEBIT, numberConsumer);

        verifyDeleteNumber();
        verifyConsumerAccept();
    }

    @Test
    @DisplayName("receiveAccountNumber - successfully with generating numbers")
    public void testReceiveAccountNumberSuccessfullyWithGeneration() {
        FreeAccountNumber number = getFreeAccountNumber();

        doNothing().when(service).generateAccountNumbers(AccountNumberType.DEBIT, FALLBACK_BATCH_SIZE);
        when(accountNumbersRepository.findFirstByTypeForUpdate(AccountNumberType.DEBIT.name()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(number));

        service.receiveAccountNumber(AccountNumberType.DEBIT, numberConsumer);

        verify(service, times(1)).generateAccountNumbers(AccountNumberType.DEBIT, FALLBACK_BATCH_SIZE);
        verifyDeleteNumber();
        verifyConsumerAccept();
    }

    private FreeAccountNumber getFreeAccountNumber() {
        return FreeAccountNumber.builder()
                .id(
                        FreeAccountNumberId.builder()
                                .type(AccountNumberType.DEBIT)
                                .accountNumber(TEST_ACCOUNT_NUMBER)
                                .build()
                )
                .build();
    }

    private void verifyDeleteNumber() {
        verify(accountNumbersRepository, times(1)).delete(numberCaptor.capture());
        FreeAccountNumber capturedNumber = numberCaptor.getValue();
        assertEquals(AccountNumberType.DEBIT, capturedNumber.getId().getType());
        assertEquals(1L, capturedNumber.getId().getAccountNumber());
    }

    private void verifyConsumerAccept() {
        verify(numberConsumer, times(1)).accept(numberCaptor.capture());
        FreeAccountNumber capturedNumber = numberCaptor.getValue();
        assertEquals(AccountNumberType.DEBIT, capturedNumber.getId().getType());
        assertEquals(1L, capturedNumber.getId().getAccountNumber());
    }
}
