package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountNumberService accountNumberService;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private AccountDto accountDto;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountId = UUID.randomUUID();
        account = new Account();
        account.setId(accountId);
        account.setStatus(AccountStatus.ACTIVE);

        accountDto = new AccountDto();
        accountDto.setId(accountId);
        accountDto.setStatus("ACTIVE");
    }

    @Test
    void testOpenAccount() {
        when(accountMapper.toEntity(any(AccountDto.class))).thenReturn(account);
        when(accountNumberService.getAccountNumber()).thenReturn("1234567890");
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.openAccount(accountDto);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        verify(accountNumberService).getAccountNumber();
        verify(accountRepository).save(account);
    }

    @Test
    void testFreezeAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.freezeAccount(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.FROZEN, account.getStatus());
        verify(accountRepository).save(account);
    }

    @Test
    void testCloseAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.closeAccount(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertNotNull(account.getClosedAt());
        verify(accountRepository).save(account);
    }

    @Test
    void testGetAccountById() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.getAccountById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void testGetAllAccounts() {
        when(accountRepository.findAll()).thenReturn(Collections.singletonList(account));
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        List<AccountDto> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(accountRepository).findAll();
    }
}