package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.ConflictException;
import faang.school.accountservice.mappers.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccountPositive() {
        Account account = initializeAccount();

        AccountDto accountDto = initializeAccountDto();

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getAccount(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1234567890", result.getNumber());
        assertEquals(Currency.USD, result.getCurrency());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals("1.0", result.getVersion());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void getAccountNegative() {
        Account account = initializeAccount();
        AccountDto accountDto = initializeAccountDto();

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccount(1L);
        });

        assertEquals("Account not found", exception.getMessage());
        Mockito.verify(accountRepository, times(1)).findById(1L);
        Mockito.verify(accountMapper, times(0)).toDto(account);
    }


    @Test
    void openAccountPositive() {
        AccountDto accountDto = initializeAccountDto();
        Account account = initializeAccount();

        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toEntity(accountDto)).thenReturn(account);

        AccountDto result = accountService.openAccount(accountDto);
        verify(accountMapper, times(1)).toEntity(accountDto);
        verify(accountRepository, times(1)).save(account);

    }

    @Test
    void openAccountNegative() {
        AccountDto accountDto = new AccountDto();
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            accountService.openAccount(accountDto);
        });

    }

    @Test
    void openAccountExistByNumberNegative() {
        AccountDto accountDto = initializeAccountDto();
        Account account = initializeAccount();
        when(accountRepository.existsByNumber(accountDto.getNumber())).thenReturn(true);
        ConflictException exception = assertThrows(ConflictException.class, () -> {

            AccountDto result = accountService.openAccount(accountDto);
        });
    }

    @Test
    void blockAccountPositive() {
        Account account = initializeAccount();
        account.setStatus(AccountStatus.ACTIVE);
        AccountDto accountDto = initializeAccountDto();
        accountDto.setStatus(AccountStatus.BLOCKED);

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(accountRepository.save(account)).thenReturn(account);
        Mockito.when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.blockAccount(1L);

        assertNotNull(result);
        assertEquals(AccountStatus.BLOCKED, result.getStatus());
        assertEquals(1L, result.getId());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void blockAccountAccountNotFound() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            accountService.blockAccount(1L);
        });

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(0)).save(any());
        verify(accountMapper, times(0)).toDto(any());
    }

    @Test
    void closeAccountPositive() {
        Account account = initializeAccount();
        account.setStatus(AccountStatus.ACTIVE);
        AccountDto accountDto = initializeAccountDto();
        accountDto.setStatus(AccountStatus.CLOSED);

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(accountRepository.save(account)).thenReturn(account);
        Mockito.when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.closeAccount(1L);

        assertNotNull(result);
        assertEquals(AccountStatus.CLOSED, result.getStatus());
        assertEquals(1L, result.getId());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }


    @Test
    void closeAccountNullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.closeAccount(null);
        });

        assertEquals("Account ID cannot be null", exception.getMessage());
        verify(accountRepository, times(0)).findById(any());
        verify(accountRepository, times(0)).save(any());
    }


    private AccountDto initializeAccountDto() {
        return AccountDto.builder()
                .id(1L)
                .number("1234567890")
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .version("1.0")
                .build();
    }

    private Account initializeAccount() {
        return Account.builder()
                .id(1L)
                .number("1234567890")
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .version("1.0")
                .build();
    }
}
