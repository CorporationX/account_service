package faang.school.accountservice.service.account;

import faang.school.accountservice.client.project.ProjectFeignClient;
import faang.school.accountservice.client.user.UserFeignClient;
import faang.school.accountservice.dto.account.AccountReq;
import faang.school.accountservice.dto.account.AccountResp;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private ProjectFeignClient projectFeignClient;
    @InjectMocks
    private AccountService accountService;

    @Test
    void openAccountWithUserOwnerSuccessTest() {
        AccountReq accountReq = AccountReq.builder()
                .accountNumber("111111111111")
                .userOwnerId(1L)
                .currency(Currency.RUB.name())
                .accountType(AccountType.CURRENT.name())
                .build();
        UserDto userDto = UserDto.builder()
                .id(1)
                .build();
        Account account = new Account();
        when(userFeignClient.getUser(1)).thenReturn(userDto);
        when(accountMapper.accountReqToAccount(accountReq)).thenReturn(account);
        assertDoesNotThrow(() -> accountService.openAccount(accountReq, false));
        verify(userFeignClient).getUser(1);
        verify(projectFeignClient, never()).getProject(1L);
        verify(accountMapper).accountReqToAccount(accountReq);
        verify(accountRepository).save(account);
    }

    @Test
    void openAccountWithProjectOwnerSuccessTest() {
        AccountReq accountReq = AccountReq.builder()
                .accountNumber("111111111111")
                .projectOwnerId(1L)
                .currency(Currency.RUB.name())
                .accountType(AccountType.CURRENT.name())
                .build();
        ProjectDto projectDto = ProjectDto.builder()
                .id(1)
                .build();
        Account account = new Account();
        when(projectFeignClient.getProject(1L)).thenReturn(projectDto);
        when(accountMapper.accountReqToAccount(accountReq)).thenReturn(account);
        assertDoesNotThrow(() -> accountService.openAccount(accountReq, true));
        verify(userFeignClient, never()).getUser(1);
        verify(projectFeignClient).getProject(1L);
        verify(accountMapper).accountReqToAccount(accountReq);
        verify(accountRepository).save(account);
    }

    @Test
    void openAccountWithNonExistentUserFailTest() {
        AccountReq accountReq = AccountReq.builder()
                .accountNumber("111111111111")
                .userOwnerId(100L)
                .currency(Currency.RUB.name())
                .accountType(AccountType.CURRENT.name())
                .build();
        Exception exception = assertThrows(EntityNotFoundException.class, () -> accountService.openAccount(accountReq, false));
        String expectedMessage = "User with id 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(userFeignClient).getUser(100);
        verify(projectFeignClient, never()).getProject(100L);
        verify(accountMapper, never()).accountReqToAccount(accountReq);
    }

    @Test
    void openAccountWithNonExistentProjectFailTest() {
        AccountReq accountReq = AccountReq.builder()
                .accountNumber("111111111111")
                .projectOwnerId(100L)
                .currency(Currency.RUB.name())
                .accountType(AccountType.CURRENT.name())
                .build();
        Exception exception = assertThrows(EntityNotFoundException.class, () -> accountService.openAccount(accountReq, true));
        String expectedMessage = "Project with id 100 does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(userFeignClient, never()).getUser(100);
        verify(projectFeignClient).getProject(100L);
        verify(accountMapper, never()).accountReqToAccount(accountReq);
    }

    @Test
    void getAccountSuccessTest() {
        Account account = Account.builder().id(1L).accountNumber("123456789123").build();
        AccountResp accountResp = AccountResp.builder().id(1L).accountNumber("123456789123").build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountResp(account)).thenReturn(accountResp);
        assertDoesNotThrow(() -> {
            AccountResp result = accountService.getAccount(1L);
            assertEquals(1, result.getId());
            assertEquals("123456789123", result.getAccountNumber());
        });
        verify(accountRepository).findById(1L);
        verify(accountMapper).accountToAccountResp(account);
    }

    @Test
    void getAccountForNonExistentAccountFailTest() {
        when(accountRepository.findById(200L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> accountService.getAccount(200L));
        verify(accountRepository).findById(200L);
    }

    @Test
    void blockAccountSuccessTest() {
        Account account = Account.builder().id(1L).accountNumber("123456789123").build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        assertDoesNotThrow(() -> accountService.blockAccount(1L));
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(account);
    }

    @Test
    void blockAccountForNonExistentAccountFailTest() {
        when(accountRepository.findById(100L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> accountService.blockAccount(100L));
        verify(accountRepository).findById(100L);
    }

    @Test
    void closeAccountSuccessTest() {
        Account account = Account.builder().id(1L).accountNumber("123456789123").build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        assertDoesNotThrow(() -> accountService.closeAccount(1L));
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(account);
    }

    @Test
    void closeAccountForNonExistentAccountFailTest() {
        when(accountRepository.findById(100L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> accountService.closeAccount(100L));
        verify(accountRepository).findById(100L);
    }
}
