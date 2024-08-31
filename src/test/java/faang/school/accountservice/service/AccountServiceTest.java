package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.exeption.NotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Spy
    AccountMapper accountMapper;

    @InjectMocks
    AccountService accountService;

    Account testAccount = new Account();
    Long testOwnerId = 1L;
    OwnerType testOwnerType = OwnerType.PROJECT;
    OpenAccountDto testOpenAccountDto = new OpenAccountDto();

    @Test
    public void testGetAccountIfAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getAccount(anyLong()));
    }

    @Test
    public void testGetAccountSuccessful() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountService.getAccount(anyLong());

        verify(accountRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetAccountByNumberIfAccountNotFound() {
        when(accountRepository.findByNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getAccountByNumber(anyString()));
    }

    @Test
    public void testGetAccountByNumberSuccessful() {
        when(accountRepository.findByNumber(anyString())).thenReturn(Optional.of(testAccount));

        accountService.getAccountByNumber(anyString());

        verify(accountRepository, times(1)).findByNumber(anyString());
    }

    @Test
    public void testGetAccountByOwnerIfAccountNotFound() {
        when(accountRepository.findByOwner(testOwnerId, testOwnerType)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getAccountByOwner(testOwnerId, testOwnerType));
    }

    @Test
    public void testGetAccountByOwnerSuccessful() {
        when(accountRepository.findByOwner(testOwnerId, testOwnerType)).thenReturn(Optional.of(testAccount));

        accountService.getAccountByOwner(testOwnerId, testOwnerType);

        verify(accountRepository, times(1)).findByOwner(testOwnerId, testOwnerType);
    }

    @Test
    public void testOpenAccountSuccessful() {
        testOpenAccountDto.setNumber("112233445566");

        accountService.openAccount(testOpenAccountDto);

        verify(accountRepository, times(1)).save(any());
    }

    @Test
    public void testBlockAccountIfAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.blockAccount(anyLong()));
    }

    @Test
    public void testBlockAccountSuccessful() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountService.blockAccount(anyLong());

        verify(accountRepository, times(1)).save(any());
        assertEquals(testAccount.getStatus(), Status.FROZEN);
    }

    @Test
    public void testCloseAccountIfAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.closeAccount(anyLong()));
    }

    @Test
    public void testCloseAccountSuccessful() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountService.closeAccount(anyLong());

        verify(accountRepository, times(1)).save(any());
        assertEquals(testAccount.getStatus(), Status.CLOSED);
    }
}
