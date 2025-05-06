package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountBalanceResponse;
import faang.school.accountservice.dto.account.AccountOpenRequest;
import faang.school.accountservice.dto.account.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStatusException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    private static final String ACCOUNT_NUMBER = "42000000000000000001";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.01);
    private static final String ERROR_MESSAGE = "Cannot update balance for account 42000000000000000001: status is CLOSED.";
    private static final String ERROR_MESSAGE2 = "Insufficient funds: attempted to subtract -100.01, current balance: 0.0";
    private AccountOpenRequest request;
    private Account account;
    private AccountResponse expectedResponse;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;


    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Spy
    private AccountMapperImpl accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
         request = new AccountOpenRequest(123L, OwnerType.USER, AccountType.PERSONAL, Currency.USD);

         account = new Account(123L,OwnerType.USER,AccountType.PERSONAL,Currency.USD, "42000000000000000001");

         expectedResponse = new AccountResponse(1L, ACCOUNT_NUMBER,123L,OwnerType.USER,
                 AccountType.PERSONAL,Currency.USD, BigDecimal.valueOf(0.00), AccountStatus.ACTIVE);
    }


    @Test
    void testOpen() {
        when(accountNumberGenerator.generateUniqueAccountNumber()).thenReturn(ACCOUNT_NUMBER);

        accountService.open(request);

        verify(accountRepository).save(accountCaptor.capture());
        Account capturedAccount = accountCaptor.getValue();
        assertEquals(account.getAccountNumber(), capturedAccount.getAccountNumber());
        assertEquals(account.getBalance(), capturedAccount.getBalance());
        assertEquals(account.getOwnerId(), capturedAccount.getOwnerId());
        assertEquals(account.getOwnerType(), capturedAccount.getOwnerType());
        assertEquals(account.getAccountType(), capturedAccount.getAccountType());
        assertEquals(account.getCurrency(), capturedAccount.getCurrency());
        assertEquals(account.getStatus(), capturedAccount.getStatus());
        assertEquals(account.getVersion(),capturedAccount.getVersion());


    }

    @Test
    void testGetShouldReturnAccountDtoWhenAccountExistsInDB() {
        account.setId(1L);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        AccountResponse actualResponse = accountService.get(ACCOUNT_NUMBER);

        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void testGetShouldThrowExceptionWhenAccountDoesNotExistInDB() {
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.get(ACCOUNT_NUMBER));
    }

    @Test
    void testBlockShouldBecomeFrozenWhenStatusIsActive() {
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        accountService.block(ACCOUNT_NUMBER);

        verify(accountRepository, times(1)).save(accountCaptor.capture());
        assertEquals(AccountStatus.FROZEN, accountCaptor.getValue().getStatus());
    }

    @Test
    void testUnblockShouldBecomeActiveWhenStatusIsFrozen() {
        account.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        accountService.unblock(ACCOUNT_NUMBER);

        verify(accountRepository, times(1)).save(accountCaptor.capture());
        assertEquals(AccountStatus.ACTIVE, accountCaptor.getValue().getStatus());

    }

    @Test
    void testCloseShouldBecomeClosedWhenStatusIsActive() {
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        accountService.close(ACCOUNT_NUMBER);

        verify(accountRepository, times(1)).save(accountCaptor.capture());
        assertEquals(AccountStatus.CLOSED, accountCaptor.getValue().getStatus());
    }

    @Test
    void testDeleteShouldDeleteFromDbWhenStatusIsClosed() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        accountService.delete(ACCOUNT_NUMBER);

        verify(accountRepository,times(1)).delete(accountCaptor.capture());
        Account capturedAccount = accountCaptor.getValue();
        assertEquals(account.getAccountNumber(), capturedAccount.getAccountNumber());
        assertEquals(account.getBalance(), capturedAccount.getBalance());
        assertEquals(account.getOwnerId(), capturedAccount.getOwnerId());
        assertEquals(account.getOwnerType(), capturedAccount.getOwnerType());
        assertEquals(account.getAccountType(), capturedAccount.getAccountType());
        assertEquals(account.getCurrency(), capturedAccount.getCurrency());
        assertEquals(account.getStatus(), capturedAccount.getStatus());
        assertEquals(account.getVersion(),capturedAccount.getVersion());

    }

    @Test
    void testUpdateBalanceShouldThrowExceptionWhenStatusIsClosed() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        AccountStatusException e =
                assertThrows(AccountStatusException.class, () -> accountService.updateBalance(ACCOUNT_NUMBER, AMOUNT));
        assertEquals(ERROR_MESSAGE,e.getMessage(), () -> "Wrong error message when account status is CLOSED.");
    }

    @Test
    void testUpdateBalanceShouldThrowExceptionWhenBalanceGoesNegative() {
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        IllegalStateException e =
                assertThrows(IllegalStateException.class, () -> accountService.updateBalance(ACCOUNT_NUMBER, AMOUNT.negate()));
        assertEquals(ERROR_MESSAGE2, e.getMessage(), () -> "Unexpected error message when balance goes negative.");
    }

    @Test
    void testUpdateBalanceShouldUpdateBalance() {
        AccountBalanceResponse expectedResponse = new AccountBalanceResponse(AMOUNT, null, 1L);
        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        AccountBalanceResponse actualResponse = accountService.updateBalance(ACCOUNT_NUMBER,AMOUNT);

        verify(accountRepository, times(1)).save(accountCaptor.capture());
        assertEquals(AMOUNT,accountCaptor.getValue().getBalance());
        assertEquals(expectedResponse.balance(), actualResponse.balance());


    }
}