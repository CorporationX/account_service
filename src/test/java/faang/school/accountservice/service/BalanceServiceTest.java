package faang.school.accountservice.service;


import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.OperationNotAllowed;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void createBalance_success() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Balance balance = balanceService.createBalance(accountId);

        assertNotNull(balance);
        assertEquals(BigDecimal.ZERO, balance.getActualBalance());
        assertEquals(BigDecimal.ZERO, balance.getAuthorizedBalance());
        assertEquals(account, balance.getAccount());

        verify(accountRepository).findById(accountId);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void createBalance_accountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> balanceService.createBalance(accountId)
        );

        verify(accountRepository).findById(accountId);
        verifyNoMoreInteractions(balanceRepository);
    }

    @Test
    void getBalance_success() {
        UUID accountId = UUID.randomUUID();
        Balance balance = new Balance();

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.getBalance(accountId);

        assertEquals(balance, result);
        verify(balanceRepository).findByAccountId(accountId);
    }

    @Test
    void getBalance_notFound() {
        UUID accountId = UUID.randomUUID();

        when(balanceRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(BalanceNotFoundException.class,
                () -> balanceService.getBalance(accountId)
        );
    }

    @Test
    void authorize_success() {
        UUID accountId = UUID.randomUUID();

        Balance balance = new Balance();
        balance.setActualBalance(new BigDecimal("100"));
        balance.setAuthorizedBalance(BigDecimal.ZERO);

        when(balanceRepository.findByAccountIdForUpdate(accountId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.authorize(accountId, new BigDecimal("30"));

        assertEquals(new BigDecimal("70"), result.getActualBalance());
        assertEquals(new BigDecimal("30"), result.getAuthorizedBalance());
    }

    @Test
    void authorize_insufficientFunds() {
        UUID accountId = UUID.randomUUID();

        Balance balance = new Balance();
        balance.setActualBalance(new BigDecimal("10"));

        when(balanceRepository.findByAccountIdForUpdate(accountId)).thenReturn(Optional.of(balance));

        assertThrows(OperationNotAllowed.class,
                () -> balanceService.authorize(accountId, new BigDecimal("50"))
        );
    }

    @Test
    void clearing_success() {
        UUID accountId = UUID.randomUUID();

        Balance balance = new Balance();
        balance.setAuthorizedBalance(new BigDecimal("40"));

        when(balanceRepository.findByAccountIdForUpdate(accountId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.clearing(accountId, new BigDecimal("10"));

        assertEquals(new BigDecimal("30"), result.getAuthorizedBalance());
    }

    @Test
    void clearing_insufficientAuthorized() {
        UUID accountId = UUID.randomUUID();

        Balance balance = new Balance();
        balance.setAuthorizedBalance(new BigDecimal("10"));

        when(balanceRepository.findByAccountIdForUpdate(accountId)).thenReturn(Optional.of(balance));

        assertThrows(OperationNotAllowed.class,
                () -> balanceService.clearing(accountId, new BigDecimal("50"))
        );
    }

    @Test
    void voidAuthorization_success() {
        UUID accountId = UUID.randomUUID();

        Balance balance = new Balance();
        balance.setAuthorizedBalance(new BigDecimal("30"));
        balance.setActualBalance(new BigDecimal("100"));

        when(balanceRepository.findByAccountIdForUpdate(accountId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.voidAuthorization(accountId, new BigDecimal("20"));

        assertEquals(new BigDecimal("10"), result.getAuthorizedBalance());
        assertEquals(new BigDecimal("120"), result.getActualBalance());
    }

    @Test
    void voidAuthorization_insufficientAuthorized() {
        UUID accountId = UUID.randomUUID();

        Balance balance = new Balance();
        balance.setAuthorizedBalance(new BigDecimal("10"));

        when(balanceRepository.findByAccountIdForUpdate(accountId)).thenReturn(Optional.of(balance));

        assertThrows(OperationNotAllowed.class,
                () -> balanceService.voidAuthorization(accountId, new BigDecimal("50"))
        );
    }
}
