package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.exeption.NotFoundException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountUtilServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Spy
    AccountMapperImpl accountMapper;

    @InjectMocks
    AccountUtilService accountUtilService;

    Account testAccount = Account.builder()
            .id(1L)
            .build();

    Long testOwnerId = 1L;
    String testOwnerType = "PROJECT";
    OpenAccountDto testOpenAccountDto = new OpenAccountDto();

    @Test
    public void testGetByIdIfAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountUtilService.getById(anyLong()));
    }

    @Test
    public void testGetByIdSuccessful() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountUtilService.getById(anyLong());

        verify(accountRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetByNumberIfAccountNotFound() {
        when(accountRepository.findByNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountUtilService.getByNumber(anyString()));
    }

    @Test
    public void testGetByNumberSuccessful() {
        when(accountRepository.findByNumber(anyString())).thenReturn(Optional.of(testAccount));

        accountUtilService.getByNumber(anyString());

        verify(accountRepository, times(1)).findByNumber(anyString());
    }

    @Test
    public void testGetByOwnerIfAccountNotFound() {
        when(accountRepository.findByOwner(testOwnerId, testOwnerType)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountUtilService.getByOwner(testOwnerId, testOwnerType));
    }

    @Test
    public void testGetByOwnerSuccessful() {
        when(accountRepository.findByOwner(testOwnerId, testOwnerType)).thenReturn(Optional.of(testAccount));

        accountUtilService.getByOwner(testOwnerId, testOwnerType);

        verify(accountRepository, times(1)).findByOwner(testOwnerId, testOwnerType);
    }

    @Test
    public void testOpenAccountSuccessful() {
        testOpenAccountDto.setNumber("112233445566");

        accountUtilService.openAccount(testOpenAccountDto);

        verify(accountRepository, times(1)).save(any());
    }

    @Test
    public void testChangeAccountStatusFromClosedToActive() {
        testAccount.setStatus(Status.CLOSED);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountUtilService.changeAccountStatus(1L, "ACTIVE");

        assertEquals(testAccount.getStatus(), Status.CLOSED);
        verify(accountRepository, times(0)).save(testAccount);
    }

    @Test
    public void testChangeAccountStatusToTheSame() {
        testAccount.setStatus(Status.FROZEN);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountUtilService.changeAccountStatus(1L, "FROZEN");

        verify(accountRepository, times(0)).save(testAccount);
    }

    @Test
    public void testChangeAccountStatusToFrozen() {
        testAccount.setStatus(Status.FROZEN);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountUtilService.changeAccountStatus(1L, "CLOSED");

        verify(accountRepository, times(1)).save(testAccount);
        assertNotEquals(testAccount.getClosedAt(), null);
    }

    @Test
    public void testChangeAccountStatusSuccessful() {
        testAccount.setStatus(Status.ACTIVE);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

        accountUtilService.changeAccountStatus(1L, "FROZEN");

        verify(accountRepository, times(1)).save(testAccount);
        assertEquals(testAccount.getStatus(), Status.FROZEN);
    }
}