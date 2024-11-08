package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.exception.account.AccountHasBeenUpdateException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountValidator validator;
    @Mock
    private FreeAccountNumberService freeAccountNumberService;
    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        account = Account.builder()
                .userId(1L)
                .projectId(1L)
                .type(AccountType.DEBIT)
                .currency(Currency.RUB)
                .build();
    }

    @Test
    public void testOpenBusinessAccount() {
        when(freeAccountNumberService.getFreeAccountNumber(account.getType())).thenReturn(any(String.class));

        Account result = accountService.openAccount(account);

        assertEquals(result.getStatus(), AccountStatus.ACTIVE);
        assertNotNull(result.getCreatedAt());
    }

    @Test
    public void testOpenPersonalAccount() {
        when(freeAccountNumberService.getFreeAccountNumber(account.getType())).thenReturn(any(String.class));

        Account result = accountService.openAccount(account);

        assertEquals(result.getStatus(), AccountStatus.ACTIVE);
        assertNotNull(result.getCreatedAt());
    }

    @Test
    public void testAccountVersionException() {
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new OptimisticLockingFailureException(""));
        when(freeAccountNumberService.getFreeAccountNumber(account.getType())).thenReturn(any(String.class));

        assertThrows(AccountHasBeenUpdateException.class, () -> accountService.openAccount(account));

        verify(accountRepository, times(0)).flush();
    }

    @Test
    public void testGetAccount() {
        account.setId(UUID.randomUUID());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.ofNullable(account));

        Account result = accountService.getAccountById(account.getId());

        assertEquals(result, account);
        verify(accountRepository).findById(account.getId());
    }

    @Test
    public void testGetAccountNotFound() {
        account.setId(UUID.randomUUID());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(account.getId()));
        verify(accountRepository).findById(account.getId());
    }

    @Test
    public void testBlockedAccount() {
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.ofNullable(account));

        Account result = accountService.blockAccount(account.getId());

        assertEquals(result.getStatus(), AccountStatus.BLOCKED);
        assertNotNull(result.getUpdatedAt());
        verify(accountRepository).findById(account.getId());
    }

    @Test
    public void testCloseAccount() {
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.ofNullable(account));

        Account result = accountService.closeAccount(account.getId());

        assertEquals(result.getStatus(), AccountStatus.CLOSED);
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getClosedAt());
        verify(accountRepository).findById(account.getId());
    }
}
