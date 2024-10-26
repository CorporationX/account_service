package faang.school.accountservice.service;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.reposiory.AccountRepository;
import faang.school.accountservice.reposiory.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @InjectMocks
    private BalanceService balanceService;

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final BalanceRepository balanceRepository = mock(BalanceRepository.class);

    private  Balance balance;
    private Account account;
    @BeforeEach
    void setUp() {
        balance = new Balance();
        balance.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setBalance(balance);
        balance.setAccount(account);
    }

    @Test
    @DisplayName("Balance service: check exist account id")
    public void testUpdateBalance_existAccount() {
        when(accountRepository.findById(1L)).thenThrow();

        assertThrows(RuntimeException.class, () -> balanceService.updateBalance(balance));
    }

    @Test
    @DisplayName("Balance service: check execute")
    public void testUpdateBalance_checkExecute() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Balance checkedBalance =  balanceService.updateBalance(balance);

        verify(balanceRepository).save(balance);
    }

//    @Test
//    @DisplayName("Balance service: check execute")
//    public void testUpdateBalance_checkExecute() {
//        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
//        Balance checkedBalance =  balanceService.updateBalance(balance);
//
//        verify(balanceRepository).save(balance);
//    }
}
