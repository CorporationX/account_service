package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private static final Long ACCOUNT_ID = 1L;
    private static final String ACCOUNT_NUMBER = "00001234567890123456";
    private static final Long OWNER_ID = 100L;
    private static final OwnerType OWNER_TYPE = OwnerType.USER;

    private Account testAccount;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void testGetAccountByIdReturnAccount() {
        when(accountRepository.findByIdOrThrow(ACCOUNT_ID)).thenReturn(testAccount);

        Account result = accountService.getAccountById(ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(ACCOUNT_ID, result.getId());
        verify(accountRepository).findByIdOrThrow(ACCOUNT_ID);
    }

    @Test
    void testGetAccountByNumberReturnAccount() {
        when(accountRepository.findByAccountNumberOrThrow(ACCOUNT_NUMBER)).thenReturn(testAccount);

        Account result = accountService.getAccountByNumber(ACCOUNT_NUMBER);

        assertNotNull(result);
        assertEquals(ACCOUNT_NUMBER, result.getAccountNumber());
        verify(accountRepository).findByAccountNumberOrThrow(ACCOUNT_NUMBER);
    }

    @Test
    void testGetAccountsByOwnerReturnAccounts() {
        List<Account> expectedAccounts = List.of(testAccount);
        when(accountRepository.findByOwnerIdAndOwnerType(OWNER_ID, OWNER_TYPE)).thenReturn(expectedAccounts);

        List<Account> result = accountService.getAccountsByOwner(OWNER_ID, OWNER_TYPE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(accountRepository).findByOwnerIdAndOwnerType(OWNER_ID, OWNER_TYPE);
    }

    @Test
    void testBlockAccountUpdateStatusToBlocked() {
        when(accountRepository.findByIdOrThrow(ACCOUNT_ID)).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.blockAccount(ACCOUNT_ID);

        assertEquals(AccountStatus.BLOCKED, result.getAccountStatus());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testCloseAccountUpdateStatusToClosedAndSetClosedAt() {
        when(accountRepository.findByIdOrThrow(ACCOUNT_ID)).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.closeAccount(ACCOUNT_ID);

        assertEquals(AccountStatus.CLOSED, result.getAccountStatus());
        assertNotNull(result.getClosedAt());
        verify(accountRepository).save(testAccount);
    }
}
