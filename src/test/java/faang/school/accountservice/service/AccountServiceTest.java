package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountReadDto;
import faang.school.accountservice.dto.project.ProjectReadDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.InvoiceType;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private ProjectService projectService;
    @Mock
    private UserService userService;
    @Mock
    private AccountRepository accountRepository;
    @Spy
    private AccountMapper accountMapper = new AccountMapperImpl();
    @InjectMocks
    private AccountService accountService;

    private final String accountNumber = "243a4621-18ad-48db-bf9a-326fda297e18";
    private Account account;
    private AccountCreateDto accountCreateDto;
    private final long userId = 1L;
    private final long projectId = 2L;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountNumber(accountNumber)
                .build();
        accountCreateDto = AccountCreateDto.builder()
                .invoiceType(InvoiceType.DEBIT)
                .currency(Currency.USD)
                .build();
    }

    @Test
    void testGetAccount_NotFound() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccount(accountNumber));
    }

    @Test
    void testGetAccount_SuccessCase() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        AccountReadDto accountReadDto = accountService.getAccount(accountNumber);

        verify(accountRepository, atLeastOnce()).findByAccountNumber(accountNumber);
        assertEquals(accountNumber, accountReadDto.getAccountNumber());
    }

    @Test
    void testOpenAccount_UserIdAndProjectIdEquals() {
        accountCreateDto.setUserId(1L);
        accountCreateDto.setProjectId(1L);

        assertThrows(BusinessException.class, () -> accountService.openAccount(accountCreateDto));
    }

    @Test
    void testOpenAccount_UserIdAndProjectIdNotSpecify() {
        accountCreateDto.setUserId(null);
        accountCreateDto.setProjectId(null);

        assertThrows(BusinessException.class, () -> accountService.openAccount(accountCreateDto));
    }

    @Test
    void testOpenAccount_SuccessCaseWithOwnerUser() {
        accountCreateDto.setUserId(userId);
        accountCreateDto.setProjectId(null);
        when(userService.getUserById(userId)).thenReturn(new UserDto());

        accountService.openAccount(accountCreateDto);

        verify(userService, atLeastOnce()).getUserById(userId);
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
    }

    @Test
    void testOpenAccount_SuccessCaseWithOwnerProject() {
        accountCreateDto.setUserId(null);
        accountCreateDto.setProjectId(projectId);
        when(projectService.getProjectById(projectId)).thenReturn(new ProjectReadDto());

        accountService.openAccount(accountCreateDto);

        verify(projectService, atLeastOnce()).getProjectById(projectId);
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
    }

    @Test
    void testFreezeInvoice_WithAccountStatusClose() {
        account.setStatus(AccountStatus.CLOSE);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(BusinessException.class, () -> accountService.freezeInvoice(accountNumber));
    }

    @Test
    void testFreezeInvoice_SuccessCase() {
        account.setStatus(AccountStatus.OPEN);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        var readDto = accountService.freezeInvoice(accountNumber);

        assertEquals(AccountStatus.FROZEN, readDto.getStatus());
    }

    @Test
    void testCloseInvoice_SuccessCase() {
        account.setStatus(AccountStatus.OPEN);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        var readDto = accountService.closeInvoice(accountNumber);

        assertEquals(AccountStatus.CLOSE, readDto.getStatus());
        assertNotNull(readDto.getClosedAt());
    }
}
