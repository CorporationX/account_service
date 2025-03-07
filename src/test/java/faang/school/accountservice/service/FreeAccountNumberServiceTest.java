package faang.school.accountservice.service;


import faang.school.accountservice.entity.account.AccountNumberSequence;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.entity.account.FreeAccountNumberId;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
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
class FreeAccountNumberServiceTest {
    @Mock
    private AccountNumberSequenceRepository accountNumberSequenceRepository;
    @Mock
    private FreeAccountNumberRepository freeAccountNumberRepository;

    @InjectMocks
    private FreeAccountNumberService freeAccountNumberService;

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

        freeAccountNumberService.addFreeAccountNumber(invoiceType, accountNumber);
        verify(freeAccountNumberRepository).save(freeAccountNumber);
    }

    @Test
    void testCreateCounterForAccountType_CounterExists() {
        when(accountNumberSequenceRepository.findByInvoiceType(invoiceType))
                .thenReturn(Optional.of(existingCounter));

        AccountNumberSequence result = freeAccountNumberService.createCounterForAccountType(invoiceType);

        assertNotNull(result);
        assertEquals(existingCounter, result);
        verify(accountNumberSequenceRepository, never()).save(any());
    }

    @Test
    void testCreateCounterForAccountType_CounterDoesNotExist() {
        when(accountNumberSequenceRepository.findByInvoiceType(invoiceType)).thenReturn(Optional.empty());
        when(accountNumberSequenceRepository.save(any())).thenAnswer(invoice -> invoice.getArgument(0));

        AccountNumberSequence result = freeAccountNumberService.createCounterForAccountType(invoiceType);

        assertNotNull(result);
        assertEquals(invoiceType, result.getInvoiceType());
        assertEquals(0L, result.getCurrentCounter());
        verify(accountNumberSequenceRepository).save(any());
    }

    @Test
    void testIncrementCounterIfMatches_True() {
        when(accountNumberSequenceRepository.incrementCounter(invoiceType.name(), expectedValue))
                .thenReturn(Optional.of(expectedValue + 1));

        boolean result = freeAccountNumberService.incrementCounterIfMatches(invoiceType, expectedValue);

        assertTrue(result);
        verify(accountNumberSequenceRepository).incrementCounter(invoiceType.name(), expectedValue);
    }

    @Test
    void testIncrementCounterIfMatches_False() {
        when(accountNumberSequenceRepository.incrementCounter(invoiceType.name(), expectedValue))
                .thenReturn(Optional.empty());

        boolean result = freeAccountNumberService.incrementCounterIfMatches(invoiceType, expectedValue);

        assertFalse(result);
    }

    @Test
    void testExecuteWithNewAccountNumber_FreeAccountExists() {
        when(freeAccountNumberRepository.getAndDeleteFirstFreeAccountNumber(invoiceType.name()))
                .thenReturn(Optional.of(freeAccountNumberId));

        freeAccountNumberService.executeWithNewAccountNumber(invoiceType, a -> assertEquals(accountNumber, a));
    }

    @Test
    void testExecuteWithNewAccountNumber_NoFreeAccount() {
        when(freeAccountNumberRepository.getAndDeleteFirstFreeAccountNumber(invoiceType.name()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(FreeAccountNumberId.builder()
                        .invoiceType(invoiceType)
                        .accountNumber("4040")
                        .build()));
        when(accountNumberSequenceRepository.findByInvoiceType(invoiceType))
                .thenReturn(Optional.of(existingCounter));

        freeAccountNumberService.executeWithNewAccountNumber(invoiceType, a -> assertEquals("4040", a));
        verify(freeAccountNumberRepository, times(2)).getAndDeleteFirstFreeAccountNumber(anyString());
    }
}
