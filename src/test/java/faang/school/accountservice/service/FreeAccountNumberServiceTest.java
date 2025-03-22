package faang.school.accountservice.service;

import faang.school.accountservice.exception.InvalidAccountTypeException;
import faang.school.accountservice.exception.NoFreeAccountNumbersException;
import faang.school.accountservice.model.account.AccountSeq;
import faang.school.accountservice.model.account.FreeAccountId;
import faang.school.accountservice.model.account.FreeAccountNumber;
import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumberServiceTest {

    @Mock
    private AccountSeqRepository accountSeqRepository;

    @Mock
    private FreeAccountRepository freeAccountRepository;

    private FreeAccountNumberService service;

    @BeforeEach
    void setUp() {
        service = new FreeAccountNumberService(accountSeqRepository, freeAccountRepository);
    }

    @Test
    void testGenerateAccountNumbers_success() {
        AccountType accountType = AccountType.CURRENT_INDIVIDUAL;
        int batchSize = 3;
        long prefix = 4200000000000000L;

        when(accountSeqRepository.getNextCounterValue()).thenReturn(1L, 2L, 3L);

        service.generateAccountNumbers(accountType, batchSize);

        ArgumentCaptor<List<FreeAccountNumber>> captor = ArgumentCaptor.forClass(List.class);
        verify(freeAccountRepository).saveAll(captor.capture());
        List<FreeAccountNumber> savedNumbers = captor.getValue();
        assertEquals(batchSize, savedNumbers.size(), "Должно быть сгенерировано столько же номеров, сколько batchSize");

        for (int i = 0; i < batchSize; i++) {
            long expectedNumber = prefix + (i + 1);
            FreeAccountNumber freeAccountNumber = savedNumbers.get(i);
            assertEquals(expectedNumber, freeAccountNumber.getId().getAccountNumber(),
                    "Неверный номер на позиции " + i);
            assertEquals(accountType, freeAccountNumber.getId().getType(),
                    "Неверный тип счета на позиции " + i);
        }
    }

    @Test
    void testRetrieveFreeAccountNumber_immediateFound_withAccountSeq() {
        AccountType accountType = AccountType.CURRENT_INDIVIDUAL;
        long accountNumber = 4200000000000001L;
        FreeAccountNumber freeAccountNumber =
                new FreeAccountNumber(new FreeAccountId(accountType, accountNumber));
        AccountSeq accountSeq = new AccountSeq();
        accountSeq.setType(accountType);
        accountSeq.setCounter(5);

        when(freeAccountRepository.findFirst(accountType.name())).thenReturn(freeAccountNumber);
        when(accountSeqRepository.findByType(accountType)).thenReturn(accountSeq);

        AtomicReference<FreeAccountNumber> captured = new AtomicReference<>();
        Consumer<FreeAccountNumber> consumer = captured::set;

        service.retrieveFreeAccountNumber(accountType, consumer);

        verify(freeAccountRepository).deleteByAccountTypeAndAccountNumber(accountType.name(), accountNumber);

        assertEquals(freeAccountNumber, captured.get());

        verify(accountSeqRepository, never()).save(any(AccountSeq.class));
    }

    @Test
    void testRetrieveFreeAccountNumber_immediateFound_withoutAccountSeq() {
        AccountType accountType = AccountType.CURRENT_INDIVIDUAL;
        long accountNumber = 4200000000000002L;
        FreeAccountNumber freeAccountNumber =
                new FreeAccountNumber(new FreeAccountId(accountType, accountNumber));

        when(freeAccountRepository.findFirst(accountType.name())).thenReturn(freeAccountNumber);
        when(accountSeqRepository.findByType(accountType)).thenReturn(null);

        AtomicReference<FreeAccountNumber> captured = new AtomicReference<>();
        Consumer<FreeAccountNumber> consumer = captured::set;

        service.retrieveFreeAccountNumber(accountType, consumer);

        verify(freeAccountRepository).deleteByAccountTypeAndAccountNumber(accountType.name(), accountNumber);
        assertEquals(freeAccountNumber, captured.get());

        verify(accountSeqRepository).save(any(AccountSeq.class));
    }


    @Test
    void testRetrieveFreeAccountNumber_generateNumbers_success() {
        AccountType accountType = AccountType.CURRENT_INDIVIDUAL;

        long generatedNumber = 4200000000000010L;
        FreeAccountNumber freeAccountNumber =
                new FreeAccountNumber(new FreeAccountId(accountType, generatedNumber));
        AccountSeq accountSeq = new AccountSeq();
        accountSeq.setType(accountType);
        accountSeq.setCounter(10);

        when(freeAccountRepository.findFirst(accountType.name()))
                .thenReturn(null)
                .thenReturn(freeAccountNumber);
        when(accountSeqRepository.findByType(accountType)).thenReturn(accountSeq);
        when(accountSeqRepository.getNextCounterValue())
                .thenReturn(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        AtomicReference<FreeAccountNumber> captured = new AtomicReference<>();
        Consumer<FreeAccountNumber> consumer = captured::set;

        service.retrieveFreeAccountNumber(accountType, consumer);

        verify(freeAccountRepository, times(2)).findFirst(accountType.name());
        verify(freeAccountRepository).deleteByAccountTypeAndAccountNumber(accountType.name(), generatedNumber);
        assertEquals(freeAccountNumber, captured.get());
    }

    @Test
    void testRetrieveFreeAccountNumber_generateNumbers_failure() {
        AccountType accountType = AccountType.CURRENT_INDIVIDUAL;
        when(freeAccountRepository.findFirst(accountType.name()))
                .thenReturn(null)
                .thenReturn(null);
        when(accountSeqRepository.getNextCounterValue())
                .thenReturn(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        Consumer<FreeAccountNumber> consumer = number -> {
        };

        assertThrows(NoFreeAccountNumbersException.class, () ->
                        service.retrieveFreeAccountNumber(accountType, consumer),
                "Ожидается NoFreeAccountNumbersException, если после генерации номер так и не найден");
    }
}
