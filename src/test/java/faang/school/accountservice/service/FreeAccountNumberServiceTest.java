package faang.school.accountservice.service;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumberServiceTest {

    @Mock
    FreeAccountRepository freeAccountRepository;
    @Mock
    AccountNumbersSeqRepository accountNumbersSeqRepository;
    @Mock
    Consumer<FreeAccountNumber> consumer;
    @InjectMocks
    FreeAccountNumberService service;

    int batchSize;
    long initial;
    static final long DEBIT_PATTERN = 4200_0000_0000_0000L;
    static final long SAVINGS_PATTERN = 5236_0000_0000_0000L;

    @BeforeEach
    void setUp() {
        batchSize = 5;
        initial = 1L;
    }

    @Test
    void testGenerateDebitNumbers() {
        testGenerateNumbers(AccountType.DEBIT, DEBIT_PATTERN);
    }

    @Test
    void testGenerateSavingsNumbers() {
        testGenerateNumbers(AccountType.SAVINGS, SAVINGS_PATTERN);
    }

    @Test
    void testRetrieveAccountNumber() {
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(
                new FreeAccountId(AccountType.DEBIT, DEBIT_PATTERN + 1));
        when(freeAccountRepository.retrieveNumber(AccountType.DEBIT)).thenReturn(freeAccountNumber);

        service.retrieveAccountNumber(AccountType.DEBIT, consumer);
        verify(freeAccountRepository, times(1)).retrieveNumber(AccountType.DEBIT);
        verify(consumer, times(1)).accept(freeAccountNumber);
    }

    void testGenerateNumbers(AccountType type, long pattern) {
        List<FreeAccountNumber> newNumbers = new ArrayList<>();
        for (long i = initial; i < initial + batchSize; i++) {
            newNumbers.add(new FreeAccountNumber(new FreeAccountId(type, pattern + i)));
        }
        when(accountNumbersSeqRepository.generateAccountNumbers(type, batchSize))
                .thenReturn(initial);

        service.generateAccountNumbers(type, batchSize);
        verify(accountNumbersSeqRepository, times(1))
                .createNumbersSequenceIfNecessary(type);
        verify(accountNumbersSeqRepository, times(1))
                .generateAccountNumbers(type, batchSize);
        verify(freeAccountRepository, times(1)).saveAll(newNumbers);
    }
}