package faang.school.accountservice.service;


import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersServiceTest {
    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @InjectMocks
    private FreeAccountNumbersService freeAccountNumbersService;

    private InvoiceType invoiceType;
    private String accountNumber;
    private Long expectedValue;
    private AccountNumberSequence existingCounter;
    private FreeAccountNumberId freeAccountNumberId;

    @BeforeEach
    void setUp() {
        invoiceType = InvoiceType.DEBIT;
        accountNumber = "4200 0000 0001 2345";
        expectedValue = 100L;
        existingCounter = AccountNumberSequence.builder()
                .invoiceType(invoiceType)
                .currentCounter(10L)
                .version(1L)
                .updateAt(LocalDateTime.now())
                .build();

        freeAccountNumberId = FreeAccountNumberId.builder()
                .invoiceType(invoiceType)
                .accountNumber(accountNumber)
                .build();
    }

    @Test
    void testAddFreeAccountNumber_SaveFreeAccountNumber() {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(invoiceType, accountNumber))
                .build();

        freeAccountNumbersService.addFreeAccountNumber(invoiceType, accountNumber);
        verify(freeAccountNumbersRepository).save(freeAccountNumber);
    }

    @Test
    void testCreateCounterForAccountType_CounterExists() {
        when(accountNumbersSequenceRepository.findByInvoiceType(invoiceType))
                .thenReturn(Optional.of(existingCounter));

        AccountNumberSequence result = freeAccountNumbersService.createCounterForAccountType(invoiceType);

        assertNotNull(result);
        assertEquals(existingCounter, result);
        verify(accountNumbersSequenceRepository, never()).save(any());
    }

    @Test
    void testCreateCounterForAccountType_CounterDoesNotExist() {
        when(accountNumbersSequenceRepository.findByInvoiceType(invoiceType)).thenReturn(Optional.empty());
        when(accountNumbersSequenceRepository.save(any())).thenAnswer(invoice -> invoice.getArgument(0));

        AccountNumberSequence result = freeAccountNumbersService.createCounterForAccountType(invoiceType);

        assertNotNull(result);
        assertEquals(invoiceType, result.getInvoiceType());
        assertEquals(0L, result.getCurrentCounter());
        verify(accountNumbersSequenceRepository).save(any());
    }

    @Test
    void testIncrementCounterIfMatches_True() {
        when(accountNumbersSequenceRepository.incrementCounter(invoiceType.name(), expectedValue))
                .thenReturn(Optional.of(expectedValue + 1));

        boolean result = freeAccountNumbersService.incrementCounterIfMatches(invoiceType, expectedValue);

        assertTrue(result);
        verify(accountNumbersSequenceRepository).incrementCounter(invoiceType.name(), expectedValue);
    }

    @Test
    void testIncrementCounterIfMatches_False() {
        when(accountNumbersSequenceRepository.incrementCounter(invoiceType.name(), expectedValue))
                .thenReturn(Optional.empty());

        boolean result = freeAccountNumbersService.incrementCounterIfMatches(invoiceType, expectedValue);

        assertFalse(result);
    }

    @Test
    void testExecuteWithNewAccountNumber_FreeAccountExists() {
        when(freeAccountNumbersRepository.getAndDeleteFirstFreeAccountNumber(invoiceType.name()))
                .thenReturn(Optional.of(freeAccountNumberId));

        freeAccountNumbersService.executeWithNewAccountNumber(invoiceType, a -> assertEquals(accountNumber, a));
    }

    @Test
    void testExecuteWithNewAccountNumber_NoFreeAccount() {
        when(freeAccountNumbersRepository.getAndDeleteFirstFreeAccountNumber(invoiceType.name()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(FreeAccountNumberId.builder()
                        .invoiceType(invoiceType)
                        .accountNumber("4040")
                        .build()));
        when(accountNumbersSequenceRepository.findByInvoiceType(invoiceType))
                .thenReturn(Optional.of(existingCounter));

        freeAccountNumbersService.executeWithNewAccountNumber(invoiceType, a -> assertEquals("4040", a));
        verify(freeAccountNumbersRepository, times(2)).getAndDeleteFirstFreeAccountNumber(anyString());
    }
}
