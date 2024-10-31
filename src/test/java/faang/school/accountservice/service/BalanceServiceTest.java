package faang.school.accountservice.service;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @InjectMocks
    private BalanceService balanceService;

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final BalanceRepository balanceRepository = mock(BalanceRepository.class);

    private Balance balance;
    private Account account;

    @BeforeEach
    void setUp() {
        balance = new Balance();
        balance.setId(UUID.fromString("2a5c971f-69c6-4dc2-a341-cd322ab3da25"));
        balance.setAuthorization(BigDecimal.valueOf(0));
        balance.setActual(BigDecimal.valueOf(0));

        account = new Account();
        account.setId(UUID.fromString("7e91477c-0121-4123-a112-6c98a4413e1b"));
        account.setBalance(balance);
    }

    @Test
    @DisplayName("Balance service: create balance")
    public void testCreateBalance_checkExecute() {
        when(balanceService.createBalance()).thenReturn(balance);
        assertEquals(BigDecimal.valueOf(0), balance.getAuthorization());
        assertEquals(BigDecimal.valueOf(0), balance.getActual());

        balanceService.createBalance();
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    @DisplayName("Balance service: check exist account id")
    public void testUpdateBalance_existAccount() {
        when(accountRepository.findById(account.getId())).thenThrow();

        assertThrows(RuntimeException.class, () -> balanceService.updateBalance(account.getId(), balance));
    }

    @Test
    @DisplayName("Balance service: check execute")
    public void testUpdateBalance_checkExecute() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance storedBalance = balanceService.updateBalance(account.getId(), balance);

        assertNotNull(storedBalance);
        verify(balanceRepository).save(balance);
    }

    @Test
    @DisplayName("Balance service: check execute")
    public void testGetBalance_checkExecute() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Balance checkedBalance = balanceService.getBalance(account.getId());

        assertEquals(balance.getAuthorization(), checkedBalance.getAuthorization());
        assertEquals(balance.getActual(), checkedBalance.getActual());
        assertNotNull(checkedBalance);
    }
}
