package faang.school.accountservice.service;

import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.IllegalStatusTransitionException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.number.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long PROJECT_ID = 2L;
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final String ACCOUNT_NUMBER = "123456789012";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private FreeAccountNumbersService freeAccountNumbersService;

    @Spy
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @InjectMocks
    private AccountServiceImpl accountService;

    private CreateAccountDto createAccountDto;

    @BeforeEach
    public void setUp() {
        createAccountDto = new CreateAccountDto(
                USER_ID,
                null,
                AccountType.CURRENT,
                Currency.USD
        );
    }

    @Test
    public void createAccount_WithValidUserAccount_AccountCreatedSuccessfully() {
        Account testAccount = createTestAccountUser();
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        ResponseAccountDto response = accountService.createAccount(createAccountDto);

        assertNotNull(response);
        assertEquals(AccountStatus.OPENED, response.status());
        assertUserAccount(response);

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void blockAccount_WithValidUserAccount_AccountBlockedSuccessfully() {
        Account testAccount = createTestAccountUser();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        ResponseAccountDto response = accountService.updateAccountStatus(ACCOUNT_ID,  AccountStatus.BLOCKED);

        assertEquals(AccountStatus.BLOCKED, response.status());
        assertUserAccount(response);
        assertNotNull(response);

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository).save(testAccount);
    }

    @Test
    public void closeAccount_WithValidUserAccount_AccountClosedSuccessfully() {
        Account testAccount = createTestAccountUser();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        ResponseAccountDto response = accountService.updateAccountStatus(ACCOUNT_ID, AccountStatus.CLOSED);

        assertEquals(AccountStatus.CLOSED, response.status());
        assertUserAccount(response);
        assertNull(response.projectId());

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository).save(testAccount);
        assertNotNull(response);
    }

    @Test
    public void createAccount_WithValidProjectAccount_AccountCreatedSuccessfully() {
        Account testAccount = createTestAccountProject();
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        ResponseAccountDto response = accountService.createAccount(createAccountDto);

        assertNotNull(response);
        assertEquals(ACCOUNT_NUMBER, response.accountNumber());
        assertEquals(AccountType.CURRENT, response.type());
        assertEquals(Currency.USD, response.currency());
        assertEquals(AccountStatus.OPENED, response.status());
        assertEquals(PROJECT_ID, response.projectId());
        assertNull(response.userId());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void closeAccount_WhenAccountAlreadyClosed_ThrowsException() {
        Account testAccount = createTestAccountProject();
        testAccount.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        IllegalStatusTransitionException exception = assertThrows(IllegalStatusTransitionException.class,
                () -> accountService.updateAccountStatus(ACCOUNT_ID, AccountStatus.CLOSED));

        assertEquals("Account is already in status: CLOSED", exception.getMessage());

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void blockAccount_WhenAccountNotFound_ThrowsException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.updateAccountStatus(ACCOUNT_ID, AccountStatus.BLOCKED));

        assertEquals(String.format("Account not found with id: %s", ACCOUNT_ID), exception.getMessage());

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void blockAccount_WhenAccountAlreadyBlocked_ThrowsException() {
        Account testAccount = createTestAccountUser();
        testAccount.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        IllegalStatusTransitionException exception = assertThrows(IllegalStatusTransitionException.class,
                () -> accountService.updateAccountStatus(ACCOUNT_ID, AccountStatus.BLOCKED));

        assertEquals("Account is already in status: BLOCKED", exception.getMessage());

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void createAccount_WhenBothUserIdAndProjectId_ThrowsException() {
        CreateAccountDto invalidDto = new CreateAccountDto(
                USER_ID,
                PROJECT_ID,
                AccountType.CURRENT,
                Currency.USD
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.createAccount(invalidDto));

        assertEquals("Must specify exactly one owner: userId or projectId", exception.getMessage());

        verifyNoInteractions(accountRepository);
    }

    private Account createTestAccountUser() {
        Account account = new Account();
        account.setId(ACCOUNT_ID);
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setType(AccountType.CURRENT);
        account.setCurrency(Currency.USD);
        account.setUserId(USER_ID);
        account.setStatus(AccountStatus.OPENED);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setVersion(1);
        return account;
    }

    private Account createTestAccountProject() {
        Account account = new Account();
        account.setId(ACCOUNT_ID);
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setType(AccountType.CURRENT);
        account.setCurrency(Currency.USD);
        account.setProjectId(PROJECT_ID);
        account.setStatus(AccountStatus.OPENED);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setVersion(1);
        return account;
    }

    private void assertUserAccount(ResponseAccountDto response) {
        assertEquals(ACCOUNT_NUMBER, response.accountNumber());
        assertEquals(AccountType.CURRENT, response.type());
        assertEquals(Currency.USD, response.currency());
        assertEquals(USER_ID, response.userId());
        assertEquals(ACCOUNT_ID, response.accountId());
        assertNull(response.projectId());
    }
}
