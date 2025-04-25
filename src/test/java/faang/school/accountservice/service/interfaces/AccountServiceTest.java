package faang.school.accountservice.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.InternalException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.implementations.AccountServiceImpl;
import faang.school.accountservice.service.implementations.FreeAccountNumberServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FreeAccountNumberServiceImpl numberService;

    @Mock
    private AccountOwnerService accountOwnerService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountRequest accountRequest;
    private AccountResponse accountResponse;
    private AccountOwner accountOwner;
    private final long accountId = 1L;
    private final long ownerId = 123L;
    private final String accountNumber = "12345678901234560001";

    @BeforeEach
    void setUp() {
        accountOwner = new AccountOwner();
        accountOwner.setId(ownerId);
        accountOwner.setOwnerType(OwnerType.USER);

        account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setType(AccountType.INDIVIDUAL);
        account.setCurrency(Currency.USD);
        account.setStatus(AccountStatus.ACTIVE);
        account.setOwner(accountOwner);
        account.setCreatedAt(LocalDateTime.now());

        accountRequest = AccountRequest.builder()
                .type(AccountType.INDIVIDUAL)
                .ownerType(OwnerType.USER)
                .currency(Currency.USD)
                .ownerId(ownerId)
                .build();

        accountResponse = new AccountResponse();
        accountResponse.setId(accountId);
        accountResponse.setAccountNumber(accountNumber);
        accountResponse.setType(AccountType.INDIVIDUAL);
        accountResponse.setCurrency(Currency.USD);
        accountResponse.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void testGetAccount_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(accountResponse);

        AccountResponse result = accountService.getAccount(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(accountNumber, result.getAccountNumber());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void testGetAccount_NotFound_ThrowsIllegalArgumentException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getAccount(accountId)
        );
        assertEquals("Account with id: 1 was not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void testCreateAccount_Success() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(accountRequest)).thenReturn("{\"type\":\"INDIVIDUAL\"}");
        when(numberService.generateAccountNumber(accountRequest.getType())).thenReturn(accountNumber);
        when(accountOwnerService.findOwner(ownerId, OwnerType.USER)).thenReturn(accountOwner);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        accountService.createAccount(accountRequest);

        verify(objectMapper, times(1)).writeValueAsString(accountRequest);
        verify(numberService, times(1)).generateAccountNumber(accountRequest.getType());
        verify(accountOwnerService, times(1)).findOwner(ownerId, OwnerType.USER);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_JsonProcessingException_ThrowsJsonMappingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(accountRequest))
                .thenThrow(new JsonProcessingException("JSON error") {
                });

        InternalException exception = assertThrows(
                InternalException.class,
                () -> accountService.createAccount(accountRequest)
        );
        assertTrue(exception.getMessage().contains("Error processing account request"));
        verify(objectMapper, times(1)).writeValueAsString(accountRequest);
        verify(numberService, never()).generateAccountNumber(any());
        verify(accountOwnerService, never()).findOwner(anyLong(), any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testBlockAccount_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponse);

        AccountResponse result = accountService.blockAccount(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.BLOCKED, account.getStatus());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void testBlockAccount_AlreadyBlocked_ThrowsIllegalArgumentException() {
        account.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.blockAccount(accountId)
        );
        assertEquals("Account is already blocked", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void testBlockAccount_NotFound_ThrowsEntityNotFoundException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.blockAccount(accountId)
        );
        assertEquals("Account with id: " + accountId + " was not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void testCloseAccount_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponse);

        AccountResponse result = accountService.closeAccount(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertNotNull(account.getClosedAt());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void testCloseAccount_AlreadyClosed_ThrowsIllegalArgumentException() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.closeAccount(accountId)
        );
        assertEquals("Account is already closed", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void testCloseAccount_NotFound_ThrowsEntityNotFoundException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.closeAccount(accountId)
        );
        assertEquals("Account with id: " + accountId + " was not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void testGetAccountById_Success() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetAccountById_NotFound_ThrowsEntityNotFoundException() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getAccountById(accountId)
        );
        assertEquals("Account with id: " + accountId + " was not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
    }
}