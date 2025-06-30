package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountOwnerType;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.exception.account.AccountAlreadyCloseException;
import faang.school.accountservice.exception.account.AccountNotFoundException;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.currency.CurrencyService;
import faang.school.accountservice.validation.account.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountValidator accountValidator;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;
    @Mock
    private CurrencyService currencyService;
    @InjectMocks
    private AccountService accountService;
    private Account account;
    private Currency currency;
    private static final String ACCOUNT_NUMBER = "987654321098";

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.OPEN);
        account.setNumber("123456789012");

        currency = new Currency();
        currency.setId(UUID.randomUUID());
    }
    @Test
    public void testGetAccountById_successfully() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Account returnaccount = accountService.getAccountById(account.getId());

        verify(accountRepository, times(1)).findById(account.getId());
        assertEquals(account.getId(), returnaccount.getId());
    }

    @Test
    public void testGetAccountById_accountNotFound() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(account.getId()));
        verify(accountRepository, times(1)).findById(account.getId());
    }

    @Test
    public void testCreateAccount_successfully() {
        when(accountNumberGenerator.generateAccountNumber()).thenReturn(ACCOUNT_NUMBER);
        when(currencyService.getCurrencyById(currency.getId())).thenReturn(currency);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount(account, currency.getId(), AccountOwnerType.USER);

        assertEquals(ACCOUNT_NUMBER, result.getNumber());
        assertEquals(currency, result.getCurrency());
        verify(accountNumberGenerator).generateAccountNumber();
        verify(currencyService).getCurrencyById(currency.getId());
        verify(accountRepository).save(account);
    }

    @Test
    public void testUpdateAccountStatus_successfully() {
        account.setStatus(AccountStatus.OPEN);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Account result = accountService.updateAccountStatus(account.getId(), AccountStatus.BLOCKED);

        assertEquals(AccountStatus.BLOCKED, result.getStatus());
        verify(accountValidator).checkCloseAccount(account);
        verify(accountRepository).findById(account.getId());
    }

    @Test
    public void testUpdateAccountStatus_alreadyInTargetStatus() {
        account.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Account result = accountService.updateAccountStatus(account.getId(), AccountStatus.BLOCKED);

        assertEquals(AccountStatus.BLOCKED, result.getStatus());
        verify(accountRepository).findById(account.getId());
    }

    @Test
    public void testBlockAccount_accountNotFound() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.updateAccountStatus(account.getId(), AccountStatus.CLOSED));
        verify(accountRepository).findById(account.getId());
    }

    @Test
    public void testBlockAccount_accountAlreadyClosed() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        doThrow(new AccountAlreadyCloseException(account.getId())).when(accountValidator).checkCloseAccount(account);

        assertThrows(AccountAlreadyCloseException.class,
                () -> accountService.updateAccountStatus(account.getId(), AccountStatus.OPEN));

        verify(accountValidator).checkCloseAccount(account);
        verify(accountRepository).findById(account.getId());
    }
}
