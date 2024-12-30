package faang.school.accountservice.service.account;

import faang.school.accountservice.config.account.FreeAccountNumbersConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import faang.school.accountservice.model.account.sequence.AccountSeq;
import faang.school.accountservice.repository.account.AccountNumberPrefixRepository;
import faang.school.accountservice.repository.account.freeaccounts.FreeAccountRepository;
import faang.school.accountservice.repository.account.sequence.AccountNumbersSequenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FreeAccountNumberServiceTest {
    @Mock
    private AccountNumberPrefixRepository accountNumberPrefixRepository;
    @Mock
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    @Mock
    private FreeAccountRepository freeAccountRepository;
    @Mock
    private FreeAccountNumbersConfig freeAccountNumbersConfig;
    @InjectMocks
    private FreeAccountNumberService freeAccountNumberService;

    @Test
    void generateFreeAccountNumbersWithBatchSize_accountCreated() {
        AccountType accountType = AccountType.SAVINGS;
        long accountLength = 16;
        long accountQuantity = 1;

        when(accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType)).thenReturn("2900");
        when(accountNumbersSequenceRepository.findByType(accountType)).thenReturn(Optional.of(new AccountSeq(accountType, 1L, 0)));

        freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, accountLength, accountQuantity);

        verify(accountNumbersSequenceRepository, times(1)).incrementCounter(accountType, 1L, accountQuantity);
        verify(freeAccountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateFreeAccountNumbersWithBatchSize_invalidAccountLength() {
        AccountType accountType = AccountType.SAVINGS;
        when(accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, 16, 1));
        assertEquals("No prefix found for account type: SAVINGS", exception.getMessage());
    }

    @Test
    void generateFreeAccountNumbersWithBatchSize_prefixDoesNotExist() {
        AccountType accountType = AccountType.SAVINGS;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, 11, 1));
        assertEquals("Invalid account number length", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, 21, 1));
        assertEquals("Invalid account number length", exception.getMessage());
    }

    @Test
    void generateFreeAccountNumbersWithBatchSize_sequenceDoesNotExist() {
        AccountType accountType = AccountType.SAVINGS;
        long accountLength = 16;
        long accountQuantity = 1;

        when(accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType)).thenReturn("2900");
        when(accountNumbersSequenceRepository.findByType(accountType)).thenReturn(Optional.empty());
        when(accountNumbersSequenceRepository.createCounter(accountType)).thenReturn(new AccountSeq(accountType, 1L, 0));

        freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, accountLength, accountQuantity);

        verify(accountNumbersSequenceRepository, times(1)).incrementCounter(accountType, 1L, accountQuantity);
        verify(freeAccountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateFreeAccountNumbersWithLimit_AccountsCreated() {
        AccountType accountType = AccountType.SAVINGS;
        long accountLength = 16;
        long limit = 10;
        when(freeAccountRepository.countFreeAccountNumberByType(accountType)).thenReturn(5);
        when(accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType)).thenReturn("200");
        when(accountNumbersSequenceRepository.findByType(accountType)).thenReturn(Optional.of(new AccountSeq(accountType, 1000L, 0)));

        freeAccountNumberService.generateFreeAccountNumbersWithLimit(accountType, accountLength, limit);

        verify(freeAccountRepository, times(1)).countFreeAccountNumberByType(accountType);
        verify(accountNumbersSequenceRepository, times(1)).incrementCounter(accountType, 1000L, 5L);
        verify(freeAccountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateFreeAccountNumbersWithLimit_NoAccountCreated() {
        AccountType accountType = AccountType.SAVINGS;
        long accountLength = 16;
        long limit = 5;
        when(freeAccountRepository.countFreeAccountNumberByType(accountType)).thenReturn(5);

        freeAccountNumberService.generateFreeAccountNumbersWithLimit(accountType, accountLength, limit);

        verify(freeAccountRepository, times(1)).countFreeAccountNumberByType(accountType);
        verify(accountNumbersSequenceRepository, times(0)).incrementCounter(accountType, 1000L, 5L);
        verify(freeAccountRepository, times(0)).saveAll(anyList());
    }

    @Test
    void processAccountNumber_freeAccountExists() {
        AccountType accountType = AccountType.SAVINGS;
        String accountNumber = "2200000000000001";
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(accountType, accountNumber);
        when(freeAccountRepository.countFreeAccountNumberByType(accountType)).thenReturn(1);
        when(freeAccountRepository.retrieveAndDeleteFirst(accountType.name())).thenReturn(Optional.of(freeAccountNumber));

        String result = freeAccountNumberService.processAccountNumber(accountType);

        verify(freeAccountRepository, times(1)).countFreeAccountNumberByType(accountType);
        verify(freeAccountRepository, times(1)).retrieveAndDeleteFirst(accountType.name());
        assertEquals(accountNumber, result);
    }

    @Test
    void processAccountNumber_freeAccountDoesNotExist() {
        AccountType accountType = AccountType.SAVINGS;
        String accountNumber = "2200000000000001";
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber(accountType, accountNumber);
        when(freeAccountRepository.countFreeAccountNumberByType(accountType)).thenReturn(0);
        when(freeAccountNumbersConfig.getAccountNumberLength()).thenReturn(accountNumber.length());
        when(accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType)).thenReturn("2900");
        when(accountNumbersSequenceRepository.findByType(accountType)).thenReturn(Optional.of(new AccountSeq(accountType, 1L, 0)));
        when(freeAccountRepository.retrieveAndDeleteFirst(accountType.name())).thenReturn(Optional.of(freeAccountNumber));

        String result = freeAccountNumberService.processAccountNumber(accountType);

        verify(freeAccountRepository, times(1)).countFreeAccountNumberByType(accountType);
        verify(freeAccountRepository, times(1)).retrieveAndDeleteFirst(accountType.name());
        assertEquals(accountNumber, result);
    }

    @Test
    void processAccountNumber_noFreeAccountFound() {
        AccountType accountType = AccountType.SAVINGS;
        when(freeAccountRepository.countFreeAccountNumberByType(accountType)).thenReturn(1);
        when(freeAccountRepository.retrieveAndDeleteFirst(accountType.name())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> freeAccountNumberService.processAccountNumber(accountType));
        assertEquals("No free account found for account type: SAVINGS", exception.getMessage());
    }
}
