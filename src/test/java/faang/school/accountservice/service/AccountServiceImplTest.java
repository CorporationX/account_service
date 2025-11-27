package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount_ShouldReturnDto() {
        CreateAccountDto dto = new CreateAccountDto(
                10L,
                OwnerType.USER,
                AccountType.BROKERAGE,
                Currency.USD
        );

        Account account = new Account();
        account.setId(1L);
        Account savedAccount = new Account();
        savedAccount.setId(1L);
        AccountDto accountDto = new AccountDto(
                1L,
                "12345",
                10L,
                OwnerType.USER,
                AccountType.BROKERAGE,
                Currency.USD,
                AccountStatusType.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(accountMapper.toAccount(dto)).thenReturn(account);
        when(accountNumberGenerator.generateNumber()).thenReturn("12345");
        when(accountRepository.save(account)).thenReturn(savedAccount);
        when(accountMapper.toAccountDto(savedAccount)).thenReturn(accountDto);

        AccountDto result = accountService.createAccount(dto);

        assertEquals(accountDto, result);
        assertEquals("12345", account.getNumber());
        verify(accountRepository).save(account);
    }

    @Test
    void getByAccountId_ShouldReturnDto_WhenFound() {
        Account account = new Account();
        account.setId(1L);
        AccountDto accountDto = new AccountDto(
                1L,
                "12345",
                10L,
                OwnerType.USER,
                AccountType.BROKERAGE,
                Currency.USD,
                AccountStatusType.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getByAccountId(1L);

        assertEquals(accountDto, result);
    }

    @Test
    void getByAccountId_ShouldThrow_WhenNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getByAccountId(1L));
    }

    @Test
    void blockAccount_ShouldSetFrozen_WhenActive() {
        Account account = new Account();
        account.setId(1L);
        account.setStatus(AccountStatusType.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        accountService.blockAccount(1L);

        assertEquals(AccountStatusType.FROZEN, account.getStatus());
        verify(accountRepository).save(account);
    }

    @Test
    void blockAccount_ShouldThrow_WhenClosed() {
        Account account = new Account();
        account.setStatus(AccountStatusType.CLOSED);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class, () -> accountService.blockAccount(1L));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void closeAccount_ShouldSetClosedAndTime() {
        Account account = new Account();
        account.setStatus(AccountStatusType.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.closeAccount(1L);

        assertEquals(AccountStatusType.CLOSED, account.getStatus());
        assertNotNull(account.getClosedAt());
    }

    @Test
    void closeAccount_ShouldThrow_WhenNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.closeAccount(1L));
    }
}