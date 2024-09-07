package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountUtilService accountUtilService;

    @Spy
    AccountMapperImpl accountMapper;

    @InjectMocks
    AccountService accountService;

    Account testAccount = new Account();
    Long testAccountId = 10L;
    Long testOwnerId = 1L;
    String testOwnerType = "PROJECT";
    OpenAccountDto testOpenAccountDto = new OpenAccountDto();

    @Test
    public void testGetAccountSuccessful() {

        accountService.getAccount(anyLong());

        verify(accountUtilService, times(1)).getById(anyLong());
    }

    @Test
    public void testGetAccountByNumberSuccessful() {

        accountService.getAccountByNumber(anyString());

        verify(accountUtilService, times(1)).getByNumber(anyString());
    }

    @Test
    public void testGetAccountByOwnerSuccessful() {

        accountService.getAccountByOwner(testOwnerId, testOwnerType);

        verify(accountUtilService, times(1)).getByOwner(testOwnerId, testOwnerType);
    }

    @Test
    public void testOpenAccountSuccessful() {

        accountService.openAccount(testOpenAccountDto);

        verify(accountUtilService, times(1)).openAccount(any());
    }

    @Test
    public void testBlockAccountSuccessful() {
        String testStatus = Status.FROZEN.name();
        when(accountUtilService.changeAccountStatus(anyLong(), anyString())).thenReturn(testAccount);

        accountService.blockAccount(testAccountId);

        verify(accountUtilService, times(1)).changeAccountStatus(testAccountId, testStatus);
    }

    @Test
    public void testUnblockAccountSuccessful() {
        String testStatus = Status.ACTIVE.name();

        accountService.unblockAccount(testAccountId);

        verify(accountUtilService, times(1)).changeAccountStatus(testAccountId, testStatus);
    }

    @Test
    public void testCloseAccountSuccessful() {
        String testStatus = Status.CLOSED.name();

        accountService.closeAccount(testAccountId);

        verify(accountUtilService, times(1)).changeAccountStatus(testAccountId, testStatus);
    }
}